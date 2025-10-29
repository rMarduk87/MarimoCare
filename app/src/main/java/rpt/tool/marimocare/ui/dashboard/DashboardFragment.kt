package rpt.tool.marimocare.ui.dashboard

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.ChangeWaterEventHook
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.EditMarimoEventHook
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentDashboardBinding
import rpt.tool.marimocare.utils.view.defaultSetUp
import rpt.tool.marimocare.utils.view.enable
import rpt.tool.marimocare.utils.view.gone
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem
import rpt.tool.marimocare.utils.view.viewpager.tips.TipsPagerAdapter
import rpt.tool.marimocare.utils.view.visible
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.getValue

class DashboardFragment: BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate) {

    private val itemAdapter = ItemAdapter<MarimoItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val viewModel: DashboardViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.include1.btnDashboardHeader.enable(false)

        binding.cardCounterTotal.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_marimo_status_t)
        binding.cardCounterOverdue.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_marimo_status_o)
        binding.cardCounterDue.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_card_marimo_status_s)

        binding.recyclerMarimos.defaultSetUp(
            fastAdapter,
            ChangeWaterEventHook(),
            EditMarimoEventHook()
        )

        viewModel.marimoItems.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.recyclerMarimos.gone()
                binding.emptyListLabel.visible()
            } else {
                binding.recyclerMarimos.visible()
                binding.emptyListLabel.gone()
                itemAdapter.set(it)
            }
        }

        binding.btnAddMarimo.setOnClickListener {
           addNewMarimo()
        }

        binding.include1.btnAddMarimoHeader.setOnClickListener {
            addNewMarimo()
        }


        val tips = listOf(
            getString(R.string.marimo_tip_body_1),
            getString(R.string.marimo_tip_body_2),
            getString(R.string.marimo_tip_body_3)
        )

        val adapter = TipsPagerAdapter(tips)
        binding.tipsPager.adapter = adapter

        binding.tipsPager.setPageTransformer { page, position ->
            page.alpha = 1 - kotlin.math.abs(position)
        }

        binding.dotsIndicator.attachTo(binding.tipsPager)

        binding.arrowLeft.setOnClickListener {
            val c = binding.tipsPager.currentItem
            binding.tipsPager.setCurrentItem(if (c > 0) c - 1 else adapter.itemCount - 1, true)
        }

        binding.arrowRight.setOnClickListener {
            val c = binding.tipsPager.currentItem
            binding.tipsPager.setCurrentItem(if (c < adapter.itemCount - 1) c + 1 else 0, true)
        }

    }

    private fun addNewMarimo() {

    }
}