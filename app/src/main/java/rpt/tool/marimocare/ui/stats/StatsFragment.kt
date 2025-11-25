package rpt.tool.marimocare.ui.stats

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentStatsBinding
import rpt.tool.marimocare.databinding.StatsMarimoBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.AppUtils.Companion.toMarimoItems
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.StatsCardConfig
import rpt.tool.marimocare.utils.view.StatsHelper
import rpt.tool.marimocare.utils.view.adapters.MarimoFrequencyAdapter
import java.text.SimpleDateFormat
import java.util.*

class StatsFragment : BaseFragment<FragmentStatsBinding>(FragmentStatsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()
        setupNavigation()
        setupTopStats()
        setupBottomStats()
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

    private fun setupNavigation() {
        binding.include1.apply {
            btnDashboardHeader.setOnClickListener { safeNavController?.safeNavigate(
                StatsFragmentDirections.actionStatsFragmentToDashboardFragment()) }
            btnAddMarimoHeader.setOnClickListener { safeNavController?.safeNavigate(
                StatsFragmentDirections.actionStatsFragmentToAddOrEditFragment()) }
            btnOpenSettings.setOnClickListener { safeNavController?.safeNavigate(
                StatsFragmentDirections.actionStatsFragmentToSettingsFragment()) }
        }
    }

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

                withContext(Dispatchers.Main) {
                    binding.cardsContainer.visibility = View.VISIBLE
                    binding.includeDI.graphMarimo.visibility = View.VISIBLE
                    binding.includeDC.graphChangeMarimo.visibility = View.VISIBLE
                    binding.noLabelContainer.visibility = View.GONE
                    binding.includeAF.tvValue.text = average.toString()
                    binding.includeTWC.tvValue.text = totalWaterChanged.toString()

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
            val last6Months = AppUtils.getLastSixMonthsLabels() // formato yyyy-MM

            withContext(Dispatchers.Main) {
                // Line chart
                val trendEntries = generateTrendData(last6Months, changes)
                setupWaterTrendChart(binding.includeDI.waterTrendChart, trendEntries,
                    last6Months)

                // Bar chart
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

}