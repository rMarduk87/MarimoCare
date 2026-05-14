package rpt.tool.marimocare.ui.achievement

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentAchievementBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.managers.AchievementManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.adapters.AchievementAdapter

class AchievementFragment :
    BaseFragment<FragmentAchievementBinding>(FragmentAchievementBinding::inflate) {

    private val viewModel: AchievementViewModel by navGraphViewModels(R.id.main_nav_graph)
    private lateinit var earnedAdapter: AchievementAdapter
    private lateinit var blockedAdapter: AchievementAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeaderButtons()

        binding.include1.appLogo.setOnClickListener {
            safeNavController?.safeNavigate(
                AchievementFragmentDirections
                    .actionAchievementFragmentToDashboardFragment()
            )
        }

        setupDataForAchievement()
        viewModel.loadAchievements()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            AchievementManager.earnAchievement(1, AppUtils.getCurrentDate(), true)
            AchievementManager.earnAchievement(3, AppUtils.getCurrentDate(), true)
            AchievementManager.earnAchievement(5, AppUtils.getCurrentDate(), true)
            AchievementManager.earnAchievement(7, AppUtils.getCurrentDate(), true)
        }


        binding.recalculateBtn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                AchievementManager.recalculateAll()
            }
        }

        binding.resetAllBtn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                AchievementManager.deleteAllAchievement()
            }
        }
    }

    private fun setupHeaderButtons()
    {
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
                            AchievementFragmentDirections
                                .actionAchievementFragmentToDashboardFragment()
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
                            AchievementFragmentDirections
                                .actionAchievementFragmentToAddOrEditFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAchievementAHeader,
                    iconRes = R.drawable.ic_coccard,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.achievement),
                    enabled = false
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = {
                        safeNavController?.safeNavigate(
                            AchievementFragmentDirections
                                .actionAchievementFragmentToSettingsFragment()
                        )
                    }
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
                            AchievementFragmentDirections
                                .actionAchievementFragmentToStatsFragment()
                        )
                    }
                )
            )
        )
    }

    private fun setupDataForAchievement(){
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600
        val spanCount = if (isTablet) 3 else 1

        earnedAdapter = AchievementAdapter(emptyList())
        binding.recyclerEarned.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = earnedAdapter
        }

        blockedAdapter = AchievementAdapter(emptyList())
        binding.recyclerBlocked.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = blockedAdapter
        }

        viewModel.earnedAchievements.observe(viewLifecycleOwner) { achievements ->
            earnedAdapter.updateData(achievements)
            binding.earned.text = getString(R.string.earned_26).replace("26",
                achievements.size.toString())
            updateTotalProgress()
        }

        viewModel.lockedAchievements.observe(viewLifecycleOwner) { achievements ->
            blockedAdapter.updateData(achievements)
            binding.blocked.text = getString(R.string.locked_24).replace("24",
                achievements.size.toString())
            updateTotalProgress()
        }
    }

    private fun updateTotalProgress() {
        val earnedCount = viewModel.earnedAchievements.value?.size ?: 0
        val lockedCount = viewModel.lockedAchievements.value?.size ?: 0
        val totalCount = earnedCount + lockedCount

        binding.tvEarnedCount.text = earnedCount.toString()
        binding.tvTotalCount.text = getString(R.string._50_badges, totalCount)

        if (totalCount > 0) {
            val progress = (earnedCount.toFloat() / totalCount.toFloat() * 100).toInt()
            binding.achievementProgress.setProgress(progress, true)
        } else {
            binding.achievementProgress.progress = 0
        }
    }
}
