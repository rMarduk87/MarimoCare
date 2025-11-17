package rpt.tool.marimocare.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentSettingsBinding
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper

class SettingsFragment :
    BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private var coloredOptionSelected = false
    private var tipsAutoScrollSped = 15

    private val speedViews: MutableMap<Int, TextView> = mutableMapOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpeedViews()
        setupHeaderButtons()
        setupCardBackgrounds()
        setupDashboardColorSelection()
        setupDashboardListeners()
        setupSpeedTipsSelection()
        setupSpeedTipsListeners()
        setupSaveCancelListeners()
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
                    onClick = {
                        safeNavController?.safeNavigate(
                            SettingsFragmentDirections.actionSettingsFragmentToDashboardFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = {
                        safeNavController?.safeNavigate(
                            SettingsFragmentDirections
                                .actionSettingsFragmentToAddOrEditFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    enabled = false
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    onClick = {
                        safeNavController?.safeNavigate(
                            SettingsFragmentDirections
                                .actionSettingsFragmentToStatsFragment())
                    }
                )
            )
        )
    }

    private fun setupCardBackgrounds() {
        binding.apply {
            include3.cardCounterTotal.setBackgroundResource(R.drawable.bg_card_marimo_status_t)
            include4.cardCounterOverdue.setBackgroundResource(R.drawable.bg_card_marimo_status_o)
            include5.cardCounterDue.setBackgroundResource(R.drawable.bg_card_marimo_status_s)
            container.setBackgroundResource(R.drawable.bg_card_settings)
            containerSpeed.setBackgroundResource(R.drawable.bg_card_settings)
        }
    }

    private fun setupDashboardColorSelection() {
        coloredOptionSelected = SharedPreferencesManager.coloredIsSelected
        updateDashboardSelection()
    }

    private fun setupDashboardListeners() {
        binding.dashDefault.setOnClickListener {
            coloredOptionSelected = false
            updateDashboardSelection()
        }
        binding.dashColored.setOnClickListener {
            coloredOptionSelected = true
            updateDashboardSelection()
        }
    }

    private fun updateDashboardSelection() {
        binding.dashDefault.setBackgroundResource(
            if (coloredOptionSelected) R.drawable.bg_card_unselected else R.drawable.bg_card_selected
        )
        binding.dashColored.setBackgroundResource(
            if (coloredOptionSelected) R.drawable.bg_card_selected else R.drawable.bg_card_unselected
        )

        binding.includeCol.checkedMark.visibility =
            if (coloredOptionSelected) View.VISIBLE else View.GONE

        binding.includeDef.checkedMark.visibility =
            if (!coloredOptionSelected) View.VISIBLE else View.GONE
    }

    private fun initSpeedViews() {
        speedViews[5] = binding.inputVeryFast
        speedViews[10] = binding.inputFast
        speedViews[15] = binding.inputNormal
        speedViews[20] = binding.inputSlow
        speedViews[25] = binding.inputVerySlow
    }

    private fun setupSpeedTipsSelection() {
        tipsAutoScrollSped = SharedPreferencesManager.tipsAutoScrollSped
        updateSpeedTipsSelection()
    }

    private fun setupSpeedTipsListeners() {
        speedViews.forEach { (speed, view) ->
            view.setOnClickListener {
                tipsAutoScrollSped = speed
                updateSpeedTipsSelection()
            }
        }
    }

    private fun updateSpeedTipsSelection() {
        speedViews.forEach { (speed, view) ->

            val selected = (speed == tipsAutoScrollSped)

            view.setBackgroundResource(
                if (selected) R.drawable.edittext_outline_selected
                else R.drawable.edittext_outline_grey
            )

            val checkIcon = if (selected)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
            else null

            view.setCompoundDrawablesWithIntrinsicBounds(null, null, checkIcon,
                null)
        }
    }

    private fun setupSaveCancelListeners() {
        binding.btnSave.setOnClickListener {
            SharedPreferencesManager.coloredIsSelected = coloredOptionSelected
            SharedPreferencesManager.tipsAutoScrollSped = tipsAutoScrollSped
            Toast.makeText(
                requireContext(),
                getString(R.string.option_correctly_updated),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnCancel.setOnClickListener {
            coloredOptionSelected = SharedPreferencesManager.coloredIsSelected
            tipsAutoScrollSped = SharedPreferencesManager.tipsAutoScrollSped
            updateDashboardSelection()
            updateSpeedTipsSelection()
        }
    }
}