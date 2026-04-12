package rpt.tool.marimocare.ui.feedback

import android.os.Bundle
import android.view.View
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentFeedbackBinding
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper

class FeedbackFragment :
    BaseFragment<FragmentFeedbackBinding>(FragmentFeedbackBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()

        binding.include1.appLogo.setOnClickListener {
            safeNavController?.safeNavigate(
                FeedbackFragmentDirections
                    .actionFeedbackFragmentToDashboardFragment())
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
                        FeedbackFragmentDirections.
                        actionFeedbackFragmentToDashboardFragment()
                    ) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = { safeNavController?.safeNavigate(
                        FeedbackFragmentDirections
                            .actionFeedbackFragmentToAddOrEditFragment()) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = { safeNavController?.safeNavigate(
                        FeedbackFragmentDirections
                            .actionFeedbackFragmentToSettingsFragment()) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    onClick = { safeNavController?.safeNavigate(
                        FeedbackFragmentDirections
                            .actionFeedbackFragmentToStatsFragment()) }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenFeedback,
                    iconRes = R.drawable.ic_feedback,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.feedback),
                    enabled = false
                )
            )
        )
    }
}