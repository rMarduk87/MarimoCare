package rpt.tool.marimocare.ui.dashboard

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.navGraphViewModels
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.ChangeWaterEventHook
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.EditMarimoEventHook
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentDashboardBinding
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.defaultSetUp
import rpt.tool.marimocare.utils.view.enable
import rpt.tool.marimocare.utils.view.gone
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem
import rpt.tool.marimocare.utils.view.viewpager.tips.TipsPagerAdapter
import rpt.tool.marimocare.utils.view.visible
import kotlin.getValue

class DashboardFragment: BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate) {

    private lateinit var itemAdapter: ItemAdapter<MarimoItem>
    private lateinit var fastAdapter: FastAdapter<MarimoItem>

    private val viewModel: DashboardViewModel by navGraphViewModels(R.id.main_nav_graph)

    private var autoScrollJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter)

        // --- HEADER ---
        binding.include1.btnDashboardHeader.enable(false)
        binding.include1.btnAddMarimoHeader.setOnClickListener { addNewMarimo() }

        // --- COUNTER CARDS BACKGROUND ---
        binding.cardCounterTotal.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_marimo_status_t)
        binding.cardCounterOverdue.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_marimo_status_o)
        binding.cardCounterDue.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_marimo_status_s)

        // --- RECYCLER VIEW SETUP ---
        binding.recyclerMarimos.apply {
            // This is the most important part - sets how items are laid out
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            adapter = fastAdapter
        }

        // 3. ADD the hooks manually to the adapter
        fastAdapter.addEventHook(ChangeWaterEventHook())
        fastAdapter.addEventHook(EditMarimoEventHook())

        // --- OBSERVER MARIMO ITEMS ---
        viewModel.marimoItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.recyclerMarimos.gone()
                binding.emptyListLabel.visible()
                binding.totalMarimo.text = "0"
            } else {
                binding.recyclerMarimos.visible()
                binding.emptyListLabel.gone()
                itemAdapter.set(items)
                binding.totalMarimo.text = items.size.toString()
                fastAdapter.notifyAdapterDataSetChanged()
            }
        }

        // --- OBSERVER OVERDUE & DUE SOON ---
        viewModel.overdueMarimo.observe(viewLifecycleOwner) { count ->
            binding.overdueMarimo.text = count.toString()
        }

        viewModel.dueSoonMarimo.observe(viewLifecycleOwner) { count ->
            binding.dueSoonMarimo.text = count.toString()
        }

        // --- BUTTON ADD MARIMO ---
        binding.btnAddMarimo.setOnClickListener { addNewMarimo() }

        // --- TIPS VIEWPAGER ---
        val tips = listOf(
            getString(R.string.marimo_tip_body_1),
            getString(R.string.marimo_tip_body_2),
            getString(R.string.marimo_tip_body_3),
            getString(R.string.marimo_tip_body_4),
            getString(R.string.marimo_tip_body_5),
            getString(R.string.marimo_tip_body_6),
            getString(R.string.marimo_tip_body_7),
            getString(R.string.marimo_tip_body_8),
            getString(R.string.marimo_tip_body_9),
            getString(R.string.marimo_tip_body_10),
        )

        val tipsTitle = listOf(
            getString(R.string.marimo_tip_title_1),
            getString(R.string.marimo_tip_title_2),
            getString(R.string.marimo_tip_title_3),
            getString(R.string.marimo_tip_title_4),
            getString(R.string.marimo_tip_title_5),
            getString(R.string.marimo_tip_title_6),
            getString(R.string.marimo_tip_title_7),
            getString(R.string.marimo_tip_title_8),
            getString(R.string.marimo_tip_title_9),
            getString(R.string.marimo_tip_title_10),
        )

        val adapter = TipsPagerAdapter(tips)
        binding.tipsPager.adapter = adapter
        binding.tipsPager.setPageTransformer { page, position -> page.alpha = 1 - kotlin.math.abs(position) }
        binding.dotsIndicator.attachTo(binding.tipsPager)

        // --- TIPS ARROWS ---
        binding.arrowLeft.setOnClickListener {
            stopAutoScroll()
            val adapter = binding.tipsPager.adapter ?: return@setOnClickListener
            val current = binding.tipsPager.currentItem
            val next = if (current > 0) current - 1 else adapter.itemCount - 1
            binding.tipsPager.setCurrentItem(next, current != 0)
            binding.tipTitle.text = tipsTitle[next]
        }

        binding.arrowRight.setOnClickListener {
            stopAutoScroll()
            val adapter = binding.tipsPager.adapter ?: return@setOnClickListener
            val current = binding.tipsPager.currentItem
            val next = if (current < adapter.itemCount - 1) current + 1 else 0
            binding.tipsPager.setCurrentItem(next, current != adapter.itemCount - 1)
            binding.tipTitle.text = tipsTitle[next]
        }

        // --- AUTO SCROLL ---
        startAutoScroll(5000L)

        // --- SHOW ALERT IF NEEDED ---
        showAlertForOverdueMarimo()
    }

    private fun addNewMarimo() {
        safeNavController?.safeNavigate(DashboardFragmentDirections.
        actionDashboardFragmentToAddOrEditFragment())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAutoScroll(intervalMillis: Long) {

        val tipsTitle = listOf(
            getString(R.string.marimo_tip_title_1),
            getString(R.string.marimo_tip_title_2),
            getString(R.string.marimo_tip_title_3),
            getString(R.string.marimo_tip_title_4),
            getString(R.string.marimo_tip_title_5),
            getString(R.string.marimo_tip_title_6),
            getString(R.string.marimo_tip_title_7),
            getString(R.string.marimo_tip_title_8),
            getString(R.string.marimo_tip_title_9),
            getString(R.string.marimo_tip_title_10),
        )

        val adapter = binding.tipsPager.adapter ?: return
        val itemCount = adapter.itemCount

        autoScrollJob?.cancel()

        autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(intervalMillis)

                val current = binding.tipsPager.currentItem
                val next = if (current < itemCount - 1) current + 1 else 0

                if (next == 0 && current == itemCount - 1) {
                    binding.tipsPager.setCurrentItem(0, false)
                } else {
                    binding.tipsPager.setCurrentItem(next, true)
                }

                binding.tipTitle.text = tipsTitle[next]
            }
        }
    }

    private fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        startAutoScroll(5000L)
        showAlertForOverdueMarimo()
    }

    private fun showAlertForOverdueMarimo() {

        val showAlert = SharedPreferencesManager.showAlert
        val message = SharedPreferencesManager.alerts

        if (showAlert && !message.isNullOrEmpty()) {
            binding.alertText.text = message
            binding.alertCard.visibility = View.VISIBLE
        } else {
            binding.alertCard.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }
}