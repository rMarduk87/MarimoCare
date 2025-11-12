package rpt.tool.marimocare.ui.stats

import android.os.Bundle
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentStatsBinding
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper

class StatsFragment : BaseFragment<FragmentStatsBinding>(FragmentStatsBinding::inflate) {

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()
        setupNavigation()
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
                            StatsFragmentDirections.actionStatsFragmentToDashboardFragment()
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
                            StatsFragmentDirections.actionStatsFragmentToAddOrEditFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    onClick = {
                        safeNavController?.safeNavigate(
                            StatsFragmentDirections.actionStatsFragmentToSettingsFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    enabled = false
                )
            )
        )
    }

    private fun setupNavigation() {
        binding.include1.apply {
            btnDashboardHeader.setOnClickListener {
                safeNavController?.safeNavigate(
                    StatsFragmentDirections.actionStatsFragmentToDashboardFragment())
            }
            btnAddMarimoHeader.setOnClickListener {
                safeNavController?.safeNavigate(
                    StatsFragmentDirections.actionStatsFragmentToAddOrEditFragment())
            }
            btnOpenSettings.setOnClickListener {
                safeNavController?.safeNavigate(
                    StatsFragmentDirections.actionStatsFragmentToSettingsFragment())
            }
        }
    }
}