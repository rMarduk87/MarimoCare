package rpt.tool.marimocare.ui.stats

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.tabs.TabLayout
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentStatsBinding
import rpt.tool.marimocare.databinding.StatsMarimoBinding
import rpt.tool.marimocare.ui.feedback.FeedbackFragmentDirections
import rpt.tool.marimocare.ui.marimo.addoredit.AddOrEditMarimoFragmentDirections
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.AppUtils.Companion.toMarimoItems
import rpt.tool.marimocare.utils.balloon.stats.NewStatsBalloonFactory
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.appmodels.MarimoDetailUi
import rpt.tool.marimocare.utils.managers.AchievementManager
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.StatsCardConfig
import rpt.tool.marimocare.utils.view.StatsHelper
import rpt.tool.marimocare.utils.view.adapters.DetailedComparisonAdapter
import rpt.tool.marimocare.utils.view.adapters.HealthMarimoAdapter
import rpt.tool.marimocare.utils.view.adapters.MarimoChipAdapter
import rpt.tool.marimocare.utils.view.adapters.MarimoFrequencyAdapter
import rpt.tool.marimocare.utils.view.grid.GridSpacingItemDecoration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.getValue

class StatsFragment : BaseFragment<FragmentStatsBinding>(FragmentStatsBinding::inflate) {

    private lateinit var adapter: HealthMarimoAdapter
    private lateinit var chipAdapter: MarimoChipAdapter
    private lateinit var detailAdapter: DetailedComparisonAdapter

    private val viewModel: StatsViewModel by navGraphViewModels(R.id.main_nav_graph)

    private val newStatsBalloon by balloon<NewStatsBalloonFactory>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()
        setupTopStats()
        setupBottomStats()
        setUpBottomTabs()
        setUpHealthScore()
        setUpCompareStats()

        binding.include1.appLogo.setOnClickListener {
            safeNavController?.safeNavigate(
                StatsFragmentDirections
                    .actionStatsFragmentToDashboardFragment())
        }

        if(SharedPreferencesManager.showBallonNewStats){
            SharedPreferencesManager.showBallonNewStats = false

            scrollToTabLayoutAndShowBalloon(binding.scrollView, binding.tabLayout) {

                newStatsBalloon.showAlign(
                    align = BalloonAlign.BOTTOM,
                    mainAnchor = binding.tabLayout as View,
                    subAnchorList = listOf(binding.tabLayout as View)
                )

            }

        }

        val context = requireContext()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            AchievementManager.recalculateAll(true,
                mapOf("visited_stats" to true), context)
        }
    }

    fun scrollToTabLayoutAndShowBalloon(
        scrollView: NestedScrollView,
        tabLayout: TabLayout,
        onScrollFinished: () -> Unit
    ) {
        scrollView.post {
            var targetY = tabLayout.top
            var currentParent = tabLayout.parent as? View

            while (currentParent != null && currentParent != scrollView) {
                targetY += currentParent.top
                currentParent = currentParent.parent as? View
            }

            val scrollAnimator = ObjectAnimator.ofInt(scrollView, "scrollY",
                targetY)
            scrollAnimator.duration = 600

            scrollAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    onScrollFinished()
                }
            })

            scrollAnimator.start()
        }
    }

    private fun setupHeaderButtons() {
        HeaderHelper.setupHeaderButtons(
            requireContext(),
            listOf(
                HeaderButtonConfig(
                    button = binding.include1.btnDashboardHeader,
                    iconRes = R.drawable.ic_dashboard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.dashboard),
                    onClick = { safeNavController?.safeNavigate(
                        StatsFragmentDirections
                            .actionStatsFragmentToDashboardFragment()) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = { safeNavController?.safeNavigate(
                        StatsFragmentDirections
                            .actionStatsFragmentToAddOrEditFragment()) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAchievementAHeader,
                    iconRes = R.drawable.ic_coccard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.achievement),
                    onClick = { safeNavController?.safeNavigate(
                        StatsFragmentDirections
                            .actionStatsFragmentToAchievementFragment()
                    ) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = { safeNavController?.safeNavigate(
                        StatsFragmentDirections
                            .actionStatsFragmentToSettingsFragment()) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    enabled = false
                )
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupTopStats() {
        StatsHelper.setUpStatsTopCard(requireContext(), listOf(
            StatsCardConfig(
                requireContext().getString(R.string.average_frequency),
                stringValue = "",
                subtitle = requireContext().getString(R.string.across_all_marimos),
                unitText = requireContext().getString(R.string.days),
                iconRes = R.drawable.ic_calendar,
                colorText = R.color.green,
                binding = binding.includeAF,
                colorStroke = R.color.light_green
            ),
            StatsCardConfig(
                requireContext().getString(R.string.total_water_changed),
                stringValue = "",
                iconRes = R.drawable.ic_water_drop_grey,
                colorText = R.color.marimo_blue,
                binding = binding.includeTWC,
                subtitle = requireContext().getString(R.string.since_tracking_began),
                unitText = "",
                colorStroke = R.color.marimo_light_blue
            ),
            StatsCardConfig(
                requireContext().getString(R.string.average_health_score),
                stringValue = "",
                iconRes = R.drawable.ic_hearth,
                colorText = R.color.marimo_great_red,
                binding = binding.includeAHS,
                subtitle = requireContext().getString(R.string.across_all_marimos),
                unitText = "",
                colorStroke = R.color.marimo_red
            ),
            StatsCardConfig(
                requireContext().getString(R.string.most_frequent),
                stringValue = "",
                iconRes = R.drawable.ic_coccard,
                colorText = R.color.marimo_violet,
                binding = binding.includeMF,
                subtitle = "",
                unitText = "",
                colorStroke = R.color.marimo_light_violet
            ),
            StatsCardConfig(
                requireContext().getString(R.string.last_frequent),
                stringValue = "",
                iconRes = R.drawable.ic_coccard,
                colorText = R.color.marimo_orange,
                binding = binding.includeLF,
                subtitle = "",
                unitText = "",
                colorStroke = R.color.marimo_light_yellow
            )
        ))

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if(RepositoryManager.marimoRepository.getAllSync().isEmpty()){
                withContext(Dispatchers.Main) {
                    binding.cardsContainer.visibility = View.GONE
                    binding.includeDI.graphMarimo.visibility = View.GONE
                    binding.includeDC.graphChangeMarimo.visibility = View.GONE
                    binding.noLabelContainer.visibility = View.VISIBLE
                }
            }
            else{
                val average = RepositoryManager.marimoRepository.getAverageFrequency()
                val marimoMostFrequentChanged =
                    RepositoryManager.marimoRepository.getMarimoMostFrequentChanged()
                val marimoLastFrequentChanged =
                    RepositoryManager.marimoRepository.getMarimoLastFrequentChanged()
                val totalWaterChanged = RepositoryManager.marimoRepository.getTotalWaterChanged()
                val averageHealthScore = RepositoryManager.marimoRepository.getAverageHealth()

                withContext(Dispatchers.Main) {
                    binding.cardsContainer.visibility = View.VISIBLE
                    binding.includeDI.graphMarimo.visibility = View.VISIBLE
                    binding.includeDC.graphChangeMarimo.visibility = View.VISIBLE
                    binding.noLabelContainer.visibility = View.GONE
                    binding.includeAF.tvValue.text = average.toString()
                    binding.includeTWC.tvValue.text = totalWaterChanged.toString()
                    binding.includeAHS.tvValue.text = buildString {
                        append(averageHealthScore.toString())
                        append(" %")
                    }

                    setupMarimoCard(binding.includeMF,
                        marimoMostFrequentChanged, true)
                    setupMarimoCard(binding.includeLF,
                        marimoLastFrequentChanged, false)
                }
            }
        }
    }

    private fun setupMarimoCard(bindingCard: StatsMarimoBinding, marimos: List<Marimo>,
                                isMost: Boolean) {
        val tvValue = bindingCard.tvValue
        val tvSubtitle = bindingCard.tvSubtitle

        if (marimos.size == 1) {
            tvValue.text = marimos[0].name
            tvSubtitle.text = buildString {
                append(getString(R.string.every))
                append(marimos[0].changeFrequencyDays)
                append(" ")
                append(getString(R.string.days))
            }
            tvValue.isClickable = false
        } else if (marimos.isNotEmpty()) {
            tvValue.text = getString(R.string.different)
            tvSubtitle.text = buildString {
                append(getString(R.string.every))
                append(marimos[0].changeFrequencyDays)
                append(" ")
                append(getString(R.string.days))
            }
            tvValue.isClickable = true
            tvValue.setOnClickListener {
                showMarimoDialog(marimos, isMost)
            }
        }
    }

    private fun showMarimoDialog(marimos: List<Marimo>, isMost: Boolean) {
        val items = marimos.toMarimoItems(
            requireContext(),
            if (isMost) "#9538ea" else "#E47A1F",
            if (isMost) "#fbf3fc" else "#FFF7EC",
            isMost
        )

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_frequency_marimo_info)
        dialog.window?.setBackgroundDrawable(Color.WHITE.toDrawable())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        val title = dialog.findViewById<TextView>(R.id.txtDialogTitle)
        val icon = dialog.findViewById<ImageView>(R.id.icon)
        title.setTextColor(if (isMost) "#9538ea".toColorInt() else "#E47A1F".toColorInt())
        title.text = if (isMost) getString(R.string.most_attention_needed_marimos) else
            getString(R.string.most_low_maintenance_marimos)
        icon.imageTintList = requireContext().getColorStateList(if
                (isMost) R.color.marimo_violet else R.color.marimo_orange)

        dialog.findViewById<TextView>(R.id.txtDialogSubtitle).text =
            buildString {
                append(getString(R.string.these_marimos_require_water_changes_every))
                append(items[0].frequency)
                append(getString(R.string.__days))
            }

        dialog.findViewById<ImageView>(R.id.btnCloseDialog).setOnClickListener {
            dialog.dismiss() }

        val recycler = dialog.findViewById<RecyclerView>(R.id.recyclerMarimos)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = MarimoFrequencyAdapter(items)

        dialog.show()
    }

    private fun setupBottomStats() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val marimos = RepositoryManager.marimoRepository.getAllSync()
            val changes = RepositoryManager.marimoRepository.getAllChanges()
            val subTitle = if (SharedPreferencesManager.statPeriod == 0) {
                getString(R.string.actual_water_changes_over_the_last_6_months)
            } else{
                getString(R.string.actual_water_changes_over_the_last_12_months)
            }
            binding.includeDI.waterTrendText.text = subTitle
            val lastXMonths = if (SharedPreferencesManager.statPeriod == 0) AppUtils.getMonthLabels(6)
            else AppUtils.getMonthLabels(12)


            withContext(Dispatchers.Main) {

                val trendEntries = generateTrendData(lastXMonths, changes)
                setupWaterTrendChart(binding.includeDI.waterTrendChart, trendEntries,
                    lastXMonths)

                val freqValues = generateFrequencyDistribution(marimos)
                setupFrequencyChart(binding.includeDC.frequencyChart, freqValues)
            }
        }
    }

    private fun generateTrendData(last6Months: List<String>, changes: List<MarimoChange>):
            List<Entry> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val map = last6Months.associateWith { 0 }.toMutableMap()

        val monthFormatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())

        changes.forEach { change ->
            val date = formatter.parse(change.waterChangeData!!) ?: return@forEach
            val monthKey = monthFormatter.format(date)
            if (map.containsKey(monthKey)) map[monthKey] = map[monthKey]!! + 1
        }

        return map.values.mapIndexed { idx, value -> Entry(idx.toFloat(), value.toFloat()) }
    }

    private fun generateFrequencyDistribution(marimos: List<Marimo>): List<Float> {
        return listOf(
            marimos.count { it.changeFrequencyDays == 7 }.toFloat(),
            marimos.count { it.changeFrequencyDays == 10 }.toFloat(),
            marimos.count { it.changeFrequencyDays == 14 }.toFloat(),
            marimos.count { it.changeFrequencyDays == 21 }.toFloat(),
            marimos.count { it.changeFrequencyDays == 30 }.toFloat()
        )
    }

    private fun setupWaterTrendChart(chart: LineChart, entries: List<Entry>, labels: List<String>) {
        val dataSet = LineDataSet(entries, "").apply {
            color = "#00A676".toColorInt()
            lineWidth = 3f
            setDrawCircles(true)
            setCircleColor("#00A676".toColorInt())
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawFilled(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 0f
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.textColor = Color.DKGRAY
            axisLeft.gridColor = "#E0E0E0".toColorInt()

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.DKGRAY
                gridColor = Color.TRANSPARENT
                granularity = 1f
                labelRotationAngle = 0f
                yOffset = 12f
                setAvoidFirstLastClipping(true)
                valueFormatter = IndexAxisValueFormatter(labels.map { month ->
                    val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    val date = sdf.parse(month)
                    SimpleDateFormat("MMM yy", Locale.getDefault()).format(date!!)
                })
            }

            extraBottomOffset = 30f
            setTouchEnabled(false)
            setPinchZoom(false)
            invalidate()
        }

        val maxVal = entries.maxOfOrNull { it.y } ?: 5f
        chart.axisLeft.axisMaximum = if (maxVal == 0f) 5f else maxVal + 1f
    }

    private fun setupFrequencyChart(chart: BarChart, values: List<Float>) {
        val entries = values.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
        val dataSet = BarDataSet(entries, "").apply {
            color = "#00C389".toColorInt()
            valueTextSize = 0f
        }

        chart.apply {
            data = BarData(dataSet).apply { barWidth = 0.6f }
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false

            axisLeft.apply {
                textColor = Color.DKGRAY
                gridColor = "#E0E0E0".toColorInt()
                axisMinimum = 0f
                axisMaximum = (values.maxOrNull()?.plus(1f) ?: 5f)
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.DKGRAY
                gridColor = Color.TRANSPARENT
                granularity = 1f

                yOffset = 6f
                setAvoidFirstLastClipping(true)

                valueFormatter = IndexAxisValueFormatter(
                    resources.getStringArray(R.array.marimo_frequencies_changes).toList()
                )
            }

            extraBottomOffset = 16f
            setFitBars(true)
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun setUpBottomTabs() {

        val positionSelected = SharedPreferencesManager.tabSelected

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab?.position) {
                    0 -> {
                        binding.tabContentTrends.visibility = View.VISIBLE
                        binding.tabContentHealth.visibility = View.GONE
                        binding.tabContentCompare.visibility = View.GONE
                    }
                    1 -> {
                        binding.tabContentTrends.visibility = View.GONE
                        binding.tabContentHealth.visibility = View.VISIBLE
                        binding.tabContentCompare.visibility = View.GONE
                    }
                    2 -> {
                        binding.tabContentTrends.visibility = View.GONE
                        binding.tabContentHealth.visibility = View.GONE
                        binding.tabContentCompare.visibility = View.VISIBLE
                    }
                }

                SharedPreferencesManager.tabSelected = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayout.getTabAt(positionSelected)?.select()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpHealthScore(){
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600

        val spanCount = when {
            resources.configuration.smallestScreenWidthDp >= 840 -> 3
            isTablet -> 2
            else -> 1
        }

        adapter = HealthMarimoAdapter()

        binding.includeHS.marimoRecycler.layoutManager =
            GridLayoutManager(requireContext(), spanCount)

        binding.includeHS.marimoRecycler.adapter = adapter

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)

        binding.includeHS.marimoRecycler.addItemDecoration(
            GridSpacingItemDecoration(spanCount, spacing, true)
        )


        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val list =  RepositoryManager.marimoRepository.getHealthScore();

            adapter.submitList(list)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCompareStats() {

        viewModel.resetComparison()
        setupAdapters()
        observeViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAdapters() {
        chipAdapter = MarimoChipAdapter { selectedMarimos ->
            viewModel.calculateDetailsForSelection(selectedMarimos)
        }

        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }

        binding.includeMC.rvMarimoChips.layoutManager = flexboxLayoutManager
        binding.includeMC.rvMarimoChips.adapter = chipAdapter

        detailAdapter = DetailedComparisonAdapter()
        val spanCount = if (resources.configuration.smallestScreenWidthDp >= 600) 3 else 1

        binding.includeMC.rvDetailedComparison.layoutManager =
            GridLayoutManager(requireContext(), spanCount)
        binding.includeMC.rvDetailedComparison.adapter = detailAdapter

        setupRadarChart()

    }

    private fun observeViewModel() {
        viewModel.allMarimos.observe(viewLifecycleOwner) { marimoList ->
            chipAdapter.submitList(marimoList)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.comparisonDetails.collect { detailsList ->
                    updateUiState(detailsList)
                }
            }
        }
    }

    private fun updateUiState(detailsList: List<MarimoDetailUi>) {
        if (detailsList.isEmpty()) {
            binding.includeMC!!.layoutEmptyState.visibility = View.VISIBLE
            binding.includeMC!!.layoutDataState.visibility = View.GONE

            binding.includeMC!!.radarChart.clear()
        } else {
            binding.includeMC!!.layoutEmptyState.visibility = View.GONE
            binding.includeMC!!.layoutDataState.visibility = View.VISIBLE

            detailAdapter.submitList(detailsList)

            updateChartData(detailsList)
        }
    }

    private fun setupRadarChart() {
        val chart = binding.includeMC!!.radarChart

        chart.description.isEnabled = false
        chart.webLineWidth = 1f
        chart.webColor = Color.LTGRAY
        chart.webLineWidthInner = 1f
        chart.webColorInner = Color.LTGRAY
        chart.webAlpha = 100
        chart.setTouchEnabled(false)

        val xAxis = chart.xAxis
        xAxis.textSize = 12f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.text_body)
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f

        val yAxis = chart.yAxis
        yAxis.setLabelCount(5, true)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.textSize = 12f
        yAxis.textColor = Color.LTGRAY
        yAxis.setDrawLabels(true)

        chart.legend.isEnabled = false
    }

    private fun updateChartData(detailsList: List<MarimoDetailUi>) {
        val chart = binding.includeMC!!.radarChart

        val entries = ArrayList<RadarEntry>()
        val labels = ArrayList<String>()

        for (detail in detailsList) {
            val health = detail.healthValue?.toFloat() ?: 0f
            entries.add(RadarEntry(health))
            labels.add(detail.name)
        }

        val dataSet = RadarDataSet(entries, "Health")
        dataSet.color = ContextCompat.getColor(requireContext(),
            R.color.green_primary)
        dataSet.fillColor = ContextCompat.getColor(requireContext(),
            R.color.green_primary)
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 90
        dataSet.lineWidth = 1.5f
        dataSet.isDrawHighlightCircleEnabled = false
        dataSet.setDrawHighlightIndicators(false)

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val data = RadarData(dataSet)
        data.setDrawValues(false)

        chart.data = data
        chart.invalidate()
    }
}