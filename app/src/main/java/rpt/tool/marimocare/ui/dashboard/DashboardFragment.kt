package rpt.tool.marimocare.ui.dashboard

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.ChangeWaterEventHook
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.EditMarimoEventHook
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentDashboardBinding
import rpt.tool.marimocare.utils.AlertDataUtils
import rpt.tool.marimocare.utils.data.enums.MarimoStatus
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.adapters.CustomSpinnerAdapter
import rpt.tool.marimocare.utils.view.enable
import rpt.tool.marimocare.utils.view.gone
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.MarimoItem
import rpt.tool.marimocare.utils.view.viewpager.tips.TipsPagerAdapter
import rpt.tool.marimocare.utils.view.visible
import kotlin.getValue
import androidx.core.view.isVisible
import rpt.tool.marimocare.utils.view.recyclerview.items.marimo.hooks.DeleteMarimoEventHook

class DashboardFragment: BaseFragment<FragmentDashboardBinding>(
    FragmentDashboardBinding::inflate) {

    private lateinit var itemAdapter: ItemAdapter<MarimoItem>
    private lateinit var fastAdapter: FastAdapter<MarimoItem>
    private val viewModel: DashboardViewModel by navGraphViewModels(R.id.main_nav_graph)
    private var autoScrollJob: Job? = null
    private lateinit var sorting: List<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sorting = resources.getStringArray(R.array.marimo_sorting).toList()

        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter)

        binding.include1.btnDashboardHeader.enable(false)
        binding.include1.btnAddMarimoHeader.setOnClickListener { addNewMarimo() }
        binding.include1.btnOpenSettings.setOnClickListener {
            safeNavController?.safeNavigate(DashboardFragmentDirections.
            actionDashboardFragmentToSettingsFragment()) }
        binding.include1.btnOpenStats.setOnClickListener {
            safeNavController?.safeNavigate(DashboardFragmentDirections
                .actionDashboardFragmentToStatsFragment()) }

        binding.cardCounterTotal.background = ContextCompat.getDrawable(requireContext(),
            R.drawable.bg_card_marimo_status_t)
        binding.cardCounterOverdue.background = ContextCompat.getDrawable(requireContext(),
            R.drawable.bg_card_marimo_status_o)
        binding.cardCounterDue.background = ContextCompat.getDrawable(requireContext(),
            R.drawable.bg_card_marimo_status_s)

        binding.recyclerMarimos.apply {
            val isTablet = resources.configuration.smallestScreenWidthDp >= 600

            val span = if (isTablet) 3 else 1

            layoutManager = if (!isTablet) androidx.recyclerview.widget.LinearLayoutManager(
                requireContext()) else
                    GridLayoutManager(requireContext(), span)
            adapter = fastAdapter
        }

        fastAdapter.addEventHook(ChangeWaterEventHook(viewLifecycleOwner,
            requireContext()) {
            applyFilterAndSort()
            updateAlertsUI()
        })
        fastAdapter.addEventHook(EditMarimoEventHook())
        fastAdapter.addEventHook(DeleteMarimoEventHook(viewLifecycleOwner,
            requireContext()){
            applyFilterAndSort()
            updateAlertsUI()
        })

        viewModel.marimoItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.recyclerMarimos.gone()
                binding.emptyListLabel.visible()
                binding.totalMarimo.text = "0"
                binding.totalMarimoAlternative.text = "0"
                binding.btnAllFilter.text = resources.getString(R.string.all)

            } else {
                binding.recyclerMarimos.visible()
                binding.emptyListLabel.gone()
                itemAdapter.set(items)
                binding.totalMarimo.text = items.size.toString()
                binding.totalMarimoAlternative.text = items.size.toString()
                fastAdapter.notifyAdapterDataSetChanged()
                binding.btnAllFilter.text = buildString {
                    append(getString(R.string.all_counter))
                    append(items.size.toString())
                    append(")")
                }
            }
        }

        viewModel.overdueMarimo.observe(viewLifecycleOwner) { count ->
            binding.overdueMarimo.text = count.toString()
            binding.overdueMarimoAlternative.text = count.toString()
            binding.btnOverdueFilter.text = buildString {
                append(getString(R.string.overdue_counter))
                append(count.toString())
                append(")")
            }
        }

        viewModel.dueSoonMarimo.observe(viewLifecycleOwner) { count ->
            binding.dueSoonMarimo.text = count.toString()
            binding.dueSoonMarimoAlternative.text = count.toString()
            binding.btnDueSoonFilter.text = buildString {
                append(getString(R.string.due_soon_counter))
                append(count.toString())
                append(")")
            }
        }

        viewModel.upToDateMarimo.observe(viewLifecycleOwner) { count ->
            binding.btnUpToDateFilter.text = buildString {
                append(getString(R.string.upToDate_counter))
                append(count.toString())
                append(")")
            }
        }

        binding.btnOverdueFilter.setBackgroundResource(R.drawable.bg_notes_card)
        binding.btnDueSoonFilter.setBackgroundResource(R.drawable.bg_notes_card)
        binding.btnUpToDateFilter.setBackgroundResource(R.drawable.bg_notes_card)
        binding.btnAllFilter.setBackgroundResource(R.drawable.bg_notes_green_card)

        manageFilters()

        binding.btnAddMarimo.setOnClickListener { addNewMarimo() }

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
        binding.tipsPager.setPageTransformer { page, position -> page.alpha = 1 -
                kotlin.math.abs(position) }
        binding.dotsIndicator.attachTo(binding.tipsPager)

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
            binding.tipsPager.setCurrentItem(next, current !=
                    adapter.itemCount - 1)
            binding.tipTitle.text = tipsTitle[next]
        }

        startAutoScroll(SharedPreferencesManager.tipsAutoScrollSped.toLong() * 1000)

        updateAlertsUI()

        binding.gridStatus.visibility = if(!SharedPreferencesManager.coloredIsSelected)
            View.VISIBLE else View.GONE
        binding.gridStatusAlternative.visibility = if(SharedPreferencesManager.coloredIsSelected)
            View.VISIBLE else View.GONE

        setupSpinner(sorting, SharedPreferencesManager.marimoSorting)

        manageLayoutBySizeAndLocation()

    }


    private fun addNewMarimo() {
        safeNavController?.safeNavigate(DashboardFragmentDirections.
        actionDashboardFragmentToAddOrEditFragment()
        )
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

        startAutoScroll(SharedPreferencesManager.tipsAutoScrollSped.toLong() * 1000)

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AlertDataUtils.recalc(requireContext())
            }

            updateAlertsUI()
            manageFilters()
            setupSpinner(sorting, SharedPreferencesManager.marimoSorting)
            applyFilterAndSort()
        }
    }

    private fun updateAlertsUI() {

        val hasOverdue = SharedPreferencesManager.showAlertOverdue
        val hasSoon = SharedPreferencesManager.showAlertSoon

        val overdueText = SharedPreferencesManager.alertOverdue
        val soonText = SharedPreferencesManager.alertSoon

        if (hasOverdue) {
            binding.alertCardRed.visibility = View.VISIBLE
            binding.alertCardRed.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.marimo_pink)
            )
            binding.alertTextRed.text = overdueText
        }

        if (!hasOverdue && hasSoon || (hasOverdue && hasSoon)) {
            binding.alertCardOrange.visibility = View.VISIBLE
            binding.alertCardOrange.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.marimo_light_orange)
            )
            binding.alertTextOrange.text = soonText
        }

        if (!hasOverdue && !hasSoon) {
            binding.alertCardRed.visibility = View.GONE
            binding.alertCardOrange.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun manageFilters() {
        manageFiltersFromSharedPreferences()

        binding.btnAllFilter.setOnClickListener {
            SharedPreferencesManager.marimoFilter = -1
            applyFilterAndSort()
            highlightFilterButton(binding.btnAllFilter)
        }

        binding.btnOverdueFilter.setOnClickListener {
            SharedPreferencesManager.marimoFilter = MarimoStatus.OVERDUE.ordinal
            applyFilterAndSort()
            highlightFilterButton(binding.btnOverdueFilter)
        }

        binding.btnDueSoonFilter.setOnClickListener {
            SharedPreferencesManager.marimoFilter = MarimoStatus.DUE_SOON.ordinal
            applyFilterAndSort()
            highlightFilterButton(binding.btnDueSoonFilter)
        }

        binding.btnUpToDateFilter.setOnClickListener {
            SharedPreferencesManager.marimoFilter = MarimoStatus.NORMAL.ordinal
            applyFilterAndSort()
            highlightFilterButton(binding.btnUpToDateFilter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyFilterAndSort() {

        val original = viewModel.marimoItems.value ?: emptyList()

        val savedFilter = SharedPreferencesManager.marimoFilter
        val filteredList = when (savedFilter) {
            -1 -> original
            MarimoStatus.NORMAL.ordinal -> original.filter {
                MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.NORMAL }
            MarimoStatus.OVERDUE.ordinal -> original.filter {
                MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.OVERDUE }
            MarimoStatus.DUE_SOON.ordinal -> original.filter {
                MarimoStatus.from(it.marimo.daysLeft) == MarimoStatus.DUE_SOON }
            else -> original
        }

        val sorting = SharedPreferencesManager.marimoSorting
        val finalList = when (sorting) {
            0 -> filteredList.sortedByDescending { MarimoStatus.from(it.marimo.daysLeft) }
            1 -> filteredList.sortedBy { it.marimo.name }
            2 -> filteredList.sortedByDescending { it.marimo.lastChanged }
            else -> filteredList
        }

        itemAdapter.set(finalList)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun manageFiltersFromSharedPreferences() {
        val savedFilter = SharedPreferencesManager.marimoFilter
        applyFilterAndSort()
        if(savedFilter == -1){
            highlightFilterButton(binding.btnAllFilter)
        }
        else{
            var viewToBind = binding.btnAllFilter
            when (savedFilter) {
                MarimoStatus.NORMAL.ordinal -> viewToBind = binding.btnUpToDateFilter
                MarimoStatus.OVERDUE.ordinal -> viewToBind = binding.btnOverdueFilter
                MarimoStatus.DUE_SOON.ordinal -> viewToBind = binding.btnDueSoonFilter
            }
            highlightFilterButton(viewToBind)
        }

        binding.filterTitle.visibility = if(SharedPreferencesManager.showFilterAndSort)
            View.VISIBLE else View.GONE

        binding.buttonFilter?.visibility = if(SharedPreferencesManager.showFilterAndSort)
            View.VISIBLE else View.GONE

        binding.buttonFilter2?.visibility = if(SharedPreferencesManager.showFilterAndSort)
            View.VISIBLE else View.GONE

        binding.buttonFilter3?.visibility = if(SharedPreferencesManager.showFilterAndSort)
            View.VISIBLE else View.GONE

        binding.sorts?.visibility = if(SharedPreferencesManager.showFilterAndSort)
            View.VISIBLE else View.GONE

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun highlightFilterButton(selected: View) {

        val allButtons = listOf(
            binding.btnAllFilter,
            binding.btnOverdueFilter,
            binding.btnDueSoonFilter,
            binding.btnUpToDateFilter
        )

        allButtons.forEach { btn ->
            btn.setBackgroundResource(R.drawable.bg_notes_card)
            (btn as? TextView)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.marimo_dark)
            )
            btn.compoundDrawableTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(),
                    R.color.marimo_dark))
        }

        when (selected) {
            binding.btnAllFilter->{
                SharedPreferencesManager.marimoFilter = -1
                selected.setBackgroundResource(R.drawable.bg_notes_green_card)
                (selected as TextView).setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
            }

            binding.btnUpToDateFilter -> {
                SharedPreferencesManager.marimoFilter = MarimoStatus.NORMAL.ordinal
                selected.setBackgroundResource(R.drawable.bg_notes_green_card)
                (selected as TextView).setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
                (selected as Button).compoundDrawableTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(
                        requireContext(), android.R.color.white))
            }

            binding.btnOverdueFilter -> {
                SharedPreferencesManager.marimoFilter = MarimoStatus.OVERDUE.ordinal
                selected.setBackgroundResource(R.drawable.bg_notes_red_card)
                (selected as TextView).setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
                (selected as Button).compoundDrawableTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(
                        requireContext(), android.R.color.white))
            }

            binding.btnDueSoonFilter -> {
                SharedPreferencesManager.marimoFilter = MarimoStatus.DUE_SOON.ordinal
                selected.setBackgroundResource(R.drawable.bg_notes_orange_card)
                (selected as TextView).setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
                (selected as Button).compoundDrawableTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),
                        android.R.color.white))
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinner(sorting: List<String>, marimoSorting: Int) {
        val spinner = binding.marimoSpinnerLayout.customSpinner
        val arrow = binding.marimoSpinnerLayout.arrow
        val adapter = CustomSpinnerAdapter(requireContext(), sorting)
        spinner.adapter = null
        spinner.adapter = adapter
        spinner.setPopupBackgroundDrawable(ContextCompat.getDrawable(requireContext(),
            R.drawable.spinner_dropdown_background))

        val rotateUp = AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_up)
        val rotateDown = AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_down)

        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) arrow.startAnimation(rotateUp)
            false
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int,
                                        id: Long) {
                adapter.setSelectedIndex(position)
                arrow.startAnimation(rotateDown)
                SharedPreferencesManager.marimoSorting = position
                applyFilterAndSort()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                arrow.startAnimation(rotateDown)
            }
        }

        spinner.setSelection(marimoSorting)
    }

    private fun manageLayoutBySizeAndLocation() {
        val isSmall = resources.configuration.smallestScreenWidthDp <= 370
        val isLocateItaly = resources.configuration.locales.get(0).country == "IT"

        if(isSmall && isLocateItaly) {
            binding.italian1!!.visibility = if (!SharedPreferencesManager.coloredIsSelected)
                View.VISIBLE else View.GONE
            binding.italian2!!.visibility = if (!SharedPreferencesManager.coloredIsSelected)
                View.VISIBLE else View.GONE
            binding.italian1Alternative!!.visibility =
                if (SharedPreferencesManager.coloredIsSelected) View.VISIBLE else View.GONE
            binding.italian2Alternative!!.visibility =
                if (SharedPreferencesManager.coloredIsSelected) View.VISIBLE else View.GONE
        }
        else if(isSmall){
            binding.italian1!!.visibility = if (!SharedPreferencesManager.coloredIsSelected)
                View.VISIBLE else View.GONE
            binding.italian1!!.text = resources.getString(R.string.due_soon)
            if(binding.italian1!!.isVisible){
                val layoutParams = binding.italian1!!.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 34, 0, 0);
            }
            binding.italian2!!.visibility = View.GONE
            binding.italian1Alternative!!.visibility =
                if (SharedPreferencesManager.coloredIsSelected) View.VISIBLE else View.GONE
            binding.italian2Alternative!!.visibility = View.GONE
            binding.italian1Alternative!!.text = resources.getString(R.string.due_soon)
            if(binding.italian1Alternative!!.isVisible){
                val layoutParams = binding.italian1Alternative!!.layoutParams
                        as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 34, 0, 0);
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }
}