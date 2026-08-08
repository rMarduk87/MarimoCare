package rpt.tool.marimocare.ui.settings

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.managers.AchievementManager
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager

class SettingsFragment : BaseJetComposeFragment(hideBars = true) {

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun BaseJetCompose() {
        SettingsScreen(
            onNavigateToDashboard = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    SettingsFragmentDirections.actionSettingsFragmentToDashboardFragment()
                )
            },
            onNavigateToAddMarimo = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    SettingsFragmentDirections.actionSettingsFragmentToAddOrEditFragment()
                )
            },
            onNavigateToAchievement = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    SettingsFragmentDirections.actionSettingsFragmentToAchievementFragment()
                )
            },
            onNavigateToStats = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    SettingsFragmentDirections.actionSettingsFragmentToStatsFragment()
                )
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAddMarimo: () -> Unit,
    onNavigateToAchievement: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State Variables
    var coloredOptionSelected by remember { mutableStateOf(SharedPreferencesManager.coloredIsSelected) }
    var tipsAutoScrollSpeed by remember { mutableStateOf(SharedPreferencesManager.tipsAutoScrollSped) }
    var showFilterAndSort by remember { mutableStateOf(SharedPreferencesManager.showFilterAndSort) }
    var filterSelected by remember { mutableStateOf(SharedPreferencesManager.marimoFilter) }
    var sortingSelected by remember { mutableStateOf(SharedPreferencesManager.marimoSorting) }
    var statPeriodSelected by remember { mutableStateOf(SharedPreferencesManager.statPeriod) }
    var showAlertToday by remember { mutableStateOf(SharedPreferencesManager.showAlertToday) }
    var showAlertSoon by remember { mutableStateOf(SharedPreferencesManager.showAlertSoon) }
    var showAlertOverdue by remember { mutableStateOf(SharedPreferencesManager.showAlertOverdue) }
    var isChatModeEnabled by remember { mutableStateOf(SharedPreferencesManager.isChatModeEnabled) }

    val resetState = {
        coloredOptionSelected = SharedPreferencesManager.coloredIsSelected
        tipsAutoScrollSpeed = SharedPreferencesManager.tipsAutoScrollSped
        showFilterAndSort = SharedPreferencesManager.showFilterAndSort
        filterSelected = SharedPreferencesManager.marimoFilter
        sortingSelected = SharedPreferencesManager.marimoSorting
        statPeriodSelected = SharedPreferencesManager.statPeriod
        showAlertToday = SharedPreferencesManager.showAlertToday
        showAlertSoon = SharedPreferencesManager.showAlertSoon
        showAlertOverdue = SharedPreferencesManager.showAlertOverdue
        isChatModeEnabled = SharedPreferencesManager.isChatModeEnabled
    }

    val saveState = {
        SharedPreferencesManager.coloredIsSelected = coloredOptionSelected
        SharedPreferencesManager.tipsAutoScrollSped = tipsAutoScrollSpeed
        SharedPreferencesManager.showFilterAndSort = showFilterAndSort
        SharedPreferencesManager.marimoFilter = filterSelected
        SharedPreferencesManager.marimoSorting = sortingSelected
        SharedPreferencesManager.statPeriod = statPeriodSelected
        SharedPreferencesManager.showAlertToday = showAlertToday
        SharedPreferencesManager.showAlertSoon = showAlertSoon
        SharedPreferencesManager.showAlertOverdue = showAlertOverdue
        SharedPreferencesManager.isChatModeEnabled = isChatModeEnabled

        Toast.makeText(context, context.getString(R.string.option_correctly_updated), Toast.LENGTH_SHORT).show()
        coroutineScope.launch(Dispatchers.IO) {
            AchievementManager.recalculateAll(true, mapOf("customized_settings" to true), context)
        }
        Unit
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.marimo_background))
    ) {
        Header(
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToAddMarimo = onNavigateToAddMarimo,
            onNavigateToAchievement = onNavigateToAchievement,
            onNavigateToStats = onNavigateToStats
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.marimo_care_settings),
                color = colorResource(id = R.color.marimo_text_primary),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = stringResource(id = R.string.customize_your_marimo_care_experience),
                color = colorResource(id = R.color.marimo_text_secondary),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Cards
            DashboardStyleCard(coloredOptionSelected) { coloredOptionSelected = it }
            SpeedSelectionCard(tipsAutoScrollSpeed) { tipsAutoScrollSpeed = it }
            FilterAndSortCard(
                showFilterAndSort = showFilterAndSort,
                onShowChange = { showFilterAndSort = it },
                filterSelected = filterSelected,
                onFilterChange = { filterSelected = it },
                sortingSelected = sortingSelected,
                onSortingChange = { sortingSelected = it }
            )
            StatsPeriodCard(statPeriodSelected) { statPeriodSelected = it }
            NotificationsCard(
                showAlertToday = showAlertToday,
                onAlertTodayChange = { showAlertToday = it },
                showAlertSoon = showAlertSoon,
                onAlertSoonChange = { showAlertSoon = it },
                showAlertOverdue = showAlertOverdue,
                onAlertOverdueChange = { showAlertOverdue = it }
            )
            ChatModeCard(isChatModeEnabled) { isChatModeEnabled = it }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = resetState,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.marimo_bg_gray)),
                    modifier = Modifier
                        .width(120.dp)
                        .height(48.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.cancel), color = colorResource(id = R.color.marimo_dark))
                }

                Button(
                    onClick = saveState,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.marimo_primary)),
                    modifier = Modifier
                        .width(120.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.btn_save), color = Color.White)
                }
            }

            // Footer
            Footer()
        }
    }
}

@Composable
fun DashboardStyleCard(isColored: Boolean, onSelectionChange: (Boolean) -> Unit) {
    SettingsCardContainer(
        title = stringResource(id = R.string.dashboard_card_style),
        subtitle = stringResource(id = R.string.choose_how_you_want_your_marimo_statistics_cards_to_appear_on_the_dashboard),
        iconRes = R.drawable.ic_palette
    ) {
        // Default Style Selection
        SelectableContainer(
            isSelected = !isColored,
            onClick = { onSelectionChange(false) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.default_style),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.marimo_black),
                        modifier = Modifier.weight(1f)
                    )
                    if (!isColored) CheckmarkIcon()
                }
                Text(
                    text = stringResource(id = R.string.clean_cards_with_icons_and_subtle_backgrounds),
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.marimo_dark),
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                // --- Esempio Dashboard Default ---
                DefaultDashboardExample()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Colored Style Selection
        SelectableContainer(
            isSelected = isColored,
            onClick = { onSelectionChange(true) }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.colored_style),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.marimo_black),
                        modifier = Modifier.weight(1f)
                    )
                    if (isColored) CheckmarkIcon()
                }
                Text(
                    text = stringResource(id = R.string.bold_colored_backgrounds_without_icons),
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.marimo_dark),
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                // --- Esempio Dashboard Colored ---
                ColoredDashboardExample()
            }
        }
    }
}

@Composable
fun DefaultDashboardExample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Total
        DefaultStatCard(
            iconColor = colorResource(id = R.color.marimo_item_board),
            bgIconColor = colorResource(id = R.color.marimo_item_edit_board),
            borderColor = colorResource(id = R.color.marimo_item_board),
            count = stringResource(id = R.string._3),
            label = stringResource(id = R.string.total),
            textColor = colorResource(id = R.color.marimo_text_primary)
        )
        // Overdue
        DefaultStatCard(
            iconColor = colorResource(id = R.color.marimo_red),
            bgIconColor = colorResource(id = R.color.marimo_peach),
            borderColor = colorResource(id = R.color.marimo_peach),
            count = stringResource(id = R.string._3),
            label = stringResource(id = R.string.overdue),
            textColor = colorResource(id = R.color.marimo_red)
        )
        // Due Soon
        DefaultStatCard(
            iconColor = colorResource(id = R.color.marimo_orange),
            bgIconColor = colorResource(id = R.color.marimo_circle_mask_soon),
            borderColor = colorResource(id = R.color.marimo_orange),
            count = stringResource(id = R.string._0),
            label = stringResource(id = R.string.due_soon),
            textColor = colorResource(id = R.color.marimo_orange)
        )
    }
}

@Composable
fun RowScope.DefaultStatCard(
    iconColor: Color,
    bgIconColor: Color,
    borderColor: Color,
    count: String,
    label: String,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp)
            .background(colorResource(id = R.color.marimo_bg_light), shape = RoundedCornerShape(16.dp))
            .border(3.dp, borderColor, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .size(48.dp)
                .background(bgIconColor, shape = CircleShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(iconColor, shape = CircleShape)
            )
        }
        Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(text = label, fontSize = 12.sp, color = textColor)
    }
}

@Composable
fun ColoredDashboardExample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Total
        ColoredStatCard(
            count = stringResource(id = R.string._3),
            label = stringResource(id = R.string.total),
            textColor = colorResource(id = R.color.marimo_text_primary),
            bgColor = colorResource(id = R.color.marimo_primary_light)
        )
        // Overdue
        ColoredStatCard(
            count = stringResource(id = R.string._3),
            label = stringResource(id = R.string.overdue),
            textColor = colorResource(id = R.color.marimo_red),
            bgColor = colorResource(id = R.color.marimo_bg_error)
        )
        // Due Soon
        ColoredStatCard(
            count = stringResource(id = R.string._0),
            label = stringResource(id = R.string.due_soon),
            textColor = colorResource(id = R.color.marimo_orange),
            bgColor = colorResource(id = R.color.marimo_bg_warning)
        )
    }
}

@Composable
fun RowScope.ColoredStatCard(
    count: String,
    label: String,
    textColor: Color,
    bgColor: Color
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .height(70.dp)
            .padding(4.dp)
            .background(bgColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(text = label, fontSize = 12.sp, color = textColor)
    }
}

@Composable
fun SpeedSelectionCard(selectedSpeed: Int, onSpeedChange: (Int) -> Unit) {
    SettingsCardContainer(
        title = stringResource(id = R.string.tips_auto_scroll_speed),
        subtitle = stringResource(id = R.string.choose_how_quickly_the_marimo_care_tips_automatically_rotate),
        iconRes = R.drawable.ic_clock
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            val speeds = listOf(
                5 to R.string.very_fast_5_seconds,
                10 to R.string.fast_10_seconds,
                15 to R.string.normal_15_seconds,
                20 to R.string.slow_20_seconds,
                25 to R.string.very_slow_25_seconds
            )
            speeds.forEach { (speedValue, stringRes) ->
                OptionItem(
                    text = stringResource(id = stringRes),
                    isSelected = speedValue == selectedSpeed,
                    onClick = { onSpeedChange(speedValue) }
                )
            }
        }
    }
}

@Composable
fun FilterAndSortCard(
    showFilterAndSort: Boolean,
    onShowChange: (Boolean) -> Unit,
    filterSelected: Int,
    onFilterChange: (Int) -> Unit,
    sortingSelected: Int,
    onSortingChange: (Int) -> Unit
) {
    SettingsCardContainer(
        title = stringResource(id = R.string.dashboard_filters_amp_sorting),
        subtitle = stringResource(id = R.string.customize_how_marimos_are_filtered_and_sorted_on_the_dashboard),
        iconRes = R.drawable.ic_filter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            SectionTitle(stringResource(id = R.string.display_controls))
            OptionItem(stringResource(id = R.string.show_filters_and_sort_controls), showFilterAndSort) { onShowChange(true) }
            OptionItem(stringResource(id = R.string.hide_filters_and_sort_controls), !showFilterAndSort) { onShowChange(false) }

            if (showFilterAndSort) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(stringResource(id = R.string.default_filter))
                OptionItem(stringResource(id = R.string.all_marimos), filterSelected == -1) { onFilterChange(-1) }
                OptionItem(stringResource(id = R.string.overdue_only), filterSelected == 0) { onFilterChange(0) }
                OptionItem(stringResource(id = R.string.due_soon_only), filterSelected == 1) { onFilterChange(1) }
                OptionItem(stringResource(id = R.string.up_to_date_only), filterSelected == 2) { onFilterChange(2) }

                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(stringResource(id = R.string.default_sort_order))
                OptionItem(stringResource(id = R.string.sort_by_status_overdue_first), sortingSelected == 0) { onSortingChange(0) }
                OptionItem(stringResource(id = R.string.sorted_by_name_a_z), sortingSelected == 1) { onSortingChange(1) }
                OptionItem(stringResource(id = R.string.sort_by_last_changed_newest_first), sortingSelected == 2) { onSortingChange(2) }
            }
        }
    }
}

@Composable
fun StatsPeriodCard(statPeriodSelected: Int, onPeriodChange: (Int) -> Unit) {
    SettingsCardContainer(
        title = stringResource(id = R.string.stats_period),
        subtitle = stringResource(id = R.string.default_time_range_shown_in_the_water_change_trend_chart),
        iconRes = R.drawable.ic_stats
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            OptionItem(stringResource(id = R.string.last_sixth_month), statPeriodSelected == 0) { onPeriodChange(0) }
            OptionItem(stringResource(id = R.string.last_year), statPeriodSelected == 1) { onPeriodChange(1) }
        }
    }
}

@Composable
fun NotificationsCard(
    showAlertToday: Boolean, onAlertTodayChange: (Boolean) -> Unit,
    showAlertSoon: Boolean, onAlertSoonChange: (Boolean) -> Unit,
    showAlertOverdue: Boolean, onAlertOverdueChange: (Boolean) -> Unit
) {
    SettingsCardContainer(
        title = stringResource(id = R.string.notification_settings),
        subtitle = stringResource(id = R.string.choose_which_notifications_you_want_to_receive),
        iconRes = R.drawable.ic_alert_bell
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            OptionItem(stringResource(id = R.string.today_marimo_alert), showAlertToday) { onAlertTodayChange(!showAlertToday) }
            OptionItem(stringResource(id = R.string.due_soon_alert), showAlertSoon) { onAlertSoonChange(!showAlertSoon) }
            OptionItem(stringResource(id = R.string.overdue_marimo_alert), showAlertOverdue) { onAlertOverdueChange(!showAlertOverdue) }
        }
    }
}

@Composable
fun ChatModeCard(isChatModeEnabled: Boolean, onChatModeChange: (Boolean) -> Unit) {
    SettingsCardContainer(
        title = stringResource(id = R.string.chat_mode),
        subtitle = stringResource(id = R.string.enable_chat_mode_subtitle),
        iconRes = R.drawable.ic_topic_feedback
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            OptionItem(stringResource(id = R.string.enable_chat_mode), isChatModeEnabled) { onChatModeChange(true) }
            OptionItem(stringResource(id = R.string.disable_chat_mode), !isChatModeEnabled) { onChatModeChange(false) }
        }
    }
}

@Composable
fun Header(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAddMarimo: () -> Unit,
    onNavigateToAchievement: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.marimo_surface))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(id = R.color.marimo_primary))
                .clickable { onNavigateToDashboard() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_water_drop_white),
                contentDescription = stringResource(id = R.string.app_name),
                tint = colorResource(id = R.color.marimo_surface),
                modifier = Modifier.padding(8.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.water_change_tracker),
                color = colorResource(id = R.color.marimo_text),
                fontSize = 13.sp
            )
        }

        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            HeaderIconButton(iconRes = R.drawable.ic_dashboard, colorRes = R.color.marimo_add_icon, text = stringResource(id = R.string.dashboard), onClick = onNavigateToDashboard)
            HeaderIconButton(iconRes = R.drawable.ic_stats, colorRes = R.color.marimo_add_icon, text = stringResource(id = R.string.stats), onClick = onNavigateToStats)
            HeaderIconButton(iconRes = R.drawable.ic_add, colorRes = R.color.marimo_add_icon, text = stringResource(id = R.string.add_marimo), onClick = onNavigateToAddMarimo)
            HeaderIconButton(iconRes = R.drawable.ic_coccard, colorRes = R.color.marimo_add_icon, text = stringResource(id = R.string.achievement), onClick = onNavigateToAchievement)
            HeaderIconButton(
                iconRes = R.drawable.ic_settings,
                colorRes = R.color.marimo_item_green,
                text = stringResource(id = R.string.settings),
                bgColorRes = R.color.marimo_header,
                onClick = {}
            )
        }
    }
}

@Composable
fun HeaderIconButton(
    iconRes: Int,
    colorRes: Int,
    text: String? = null,
    bgColorRes: Int = android.R.color.transparent,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet && text != null) {
        val isInactive = colorRes == R.color.marimo_add_icon

        val containerColor = if (isInactive) {
            colorResource(id = R.color.marimo_surface)
        } else {
            colorResource(id = bgColorRes)
        }

        val contentColor = colorResource(id = colorRes)

        Surface(
            modifier = Modifier
                .padding(start = 9.dp)
                .height(42.dp)
                .widthIn(min = 120.dp)
                .clip(RoundedCornerShape(50.dp))
                .clickable(onClick = onClick),
            color = containerColor,
            shape = RoundedCornerShape(50.dp),
            border = if (isInactive) BorderStroke(
                1.dp,
                colorResource(id = R.color.marimo_button_white_stroke)
            ) else null
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(id = bgColorRes))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = colorResource(id = colorRes)
            )
        }
    }
}

@Composable
fun SettingsCardContainer(
    title: String,
    subtitle: String,
    iconRes: Int,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = colorResource(id = R.color.marimo_text_primary),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 10.dp)
                )
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.marimo_black)
                )
            }
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = colorResource(id = R.color.marimo_dark),
                modifier = Modifier.padding(start = 5.dp, bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun OptionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) colorResource(id = R.color.marimo_primary) else colorResource(id = R.color.marimo_gray_border)
    val bgColor = if (isSelected) colorResource(id = R.color.marimo_bg_mint_selected) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = colorResource(id = R.color.text_title),
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            CheckmarkIcon()
        }
    }
}

@Composable
fun SelectableContainer(isSelected: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    val bgColor = if (isSelected) colorResource(id = R.color.marimo_item_board) else colorResource(id = R.color.marimo_surface)
    val borderColor = if (isSelected) colorResource(id = R.color.marimo_primary) else colorResource(id = R.color.marimo_gray_border)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        content()
    }
}

@Composable
fun CheckmarkIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_check),
        contentDescription = null,
        tint = colorResource(id = R.color.marimo_primary),
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = colorResource(id = R.color.marimo_dark),
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, bottom = 8.dp)
    )
}

@Composable
fun Footer() {
    Text(
        text = stringResource(id = R.string.bottom),
        color = colorResource(id = R.color.marimo_text_secondary),
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 24.dp),
        textAlign = TextAlign.Center
    )
}