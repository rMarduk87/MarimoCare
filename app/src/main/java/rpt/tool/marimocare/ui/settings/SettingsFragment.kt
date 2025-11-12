package rpt.tool.marimocare.ui.settings

import android.os.Bundle
import android.view.View
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentSettingsBinding
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private var coloredOptionSelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()
        setupCardBackgrounds()
        setupColorSelection()
        setupDashboardListeners()
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
                    onClick = {
                        safeNavController?.safeNavigate(
                            SettingsFragmentDirections.actionSettingsFragmentToAddOrEditFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    enabled = false
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    onClick = {
                        safeNavController?.safeNavigate(
                            SettingsFragmentDirections.actionSettingsFragmentToStatsFragment()
                        )
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
            container.setBackgroundResource(R.drawable.bg_card_marimo)
        }
    }

    private fun setupColorSelection() {
        coloredOptionSelected = SharedPreferencesManager.coloredIsSelected
        binding.includeCol.checkedMark.visibility = if (coloredOptionSelected)
            View.VISIBLE else View.GONE
        binding.includeDef.checkedMark.visibility = if (!coloredOptionSelected)
            View.VISIBLE else View.GONE
    }

    private fun setupDashboardListeners() {
        binding.dashDefault.setOnClickListener { selectDefaultDashboard() }
        binding.dashColored.setOnClickListener { selectColoredDashboard() }
    }

    private fun selectDefaultDashboard() {
        if (coloredOptionSelected) {
            coloredOptionSelected = false
            updateDashboardSelection()
        }
    }

    private fun selectColoredDashboard() {
        if (!coloredOptionSelected) {
            coloredOptionSelected = true
            updateDashboardSelection()
        }
    }

    private fun updateDashboardSelection() {
        binding.dashDefault.setBackgroundResource(
            if (coloredOptionSelected) R.drawable.bg_card_unselected else
                R.drawable.bg_card_selected
        )
        binding.dashColored.setBackgroundResource(
            if (coloredOptionSelected) R.drawable.bg_card_selected else
                R.drawable.bg_card_unselected
        )
        binding.includeCol.checkedMark.visibility = if (coloredOptionSelected)
            View.VISIBLE else View.GONE
        binding.includeDef.checkedMark.visibility = if (coloredOptionSelected)
            View.GONE else View.VISIBLE
    }

    private fun setupSaveCancelListeners() {
        binding.btnSave.setOnClickListener {
            SharedPreferencesManager.coloredIsSelected = coloredOptionSelected
        }

        binding.btnCancel.setOnClickListener {
            coloredOptionSelected = SharedPreferencesManager.coloredIsSelected
            updateDashboardSelection()
        }
    }
}