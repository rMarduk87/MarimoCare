package rpt.tool.marimocare.ui.feedback

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rpt.com.base.BaseJetComposeFragment
import rpt.com.base.navigation.safeNavController
import rpt.com.base.navigation.safeNavigate
import rpt.tool.marimocare.R
import rpt.tool.marimocare.utils.managers.AchievementManager
import rpt.tool.marimocare.utils.view.shareViaWhatsApp

class FeedbackFragment : BaseJetComposeFragment(hideBars = true) {

    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    override fun BaseJetCompose() {
        FeedbackScreen(
            onNavigateToDashboard = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    FeedbackFragmentDirections.actionFeedbackFragmentToDashboardFragment()
                )
            },
            onNavigateToAddMarimo = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    FeedbackFragmentDirections.actionFeedbackFragmentToAddOrEditFragment()
                )
            },
            onNavigateToAchievement = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    FeedbackFragmentDirections.actionFeedbackFragmentToAchievementFragment()
                )
            },
            onNavigateToSettings = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    FeedbackFragmentDirections.actionFeedbackFragmentToSettingsFragment()
                )
            },
            onNavigateToStats = {
                safeNavController(R.id.main_activity_nav_host_fragment)?.safeNavigate(
                    FeedbackFragmentDirections.actionFeedbackFragmentToStatsFragment()
                )
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun FeedbackScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAddMarimo: () -> Unit,
    onNavigateToAchievement: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectedTopics = remember { mutableStateListOf<String>() }
    var messageText by remember { mutableStateOf("") }

    val bugReportText = stringResource(id = R.string.bug_report)
    val featureRequestText = stringResource(id = R.string.feature_request)
    val uiDesignText = stringResource(id = R.string.ui_design)
    val newContentText = stringResource(id = R.string.new_content)
    val generalFeedbackText = stringResource(id = R.string.general_feedback)
    val otherText = stringResource(id = R.string.other)

    val sendEmail = { subject: String, message: String ->
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.riccardo_pezzolati_gmail_com)))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.choose_email_client)))
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.no_email_app_found_on_your_device), Toast.LENGTH_SHORT).show()
        }
    }

    val openWebPage = { url: String ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.unable_to_open_link), Toast.LENGTH_SHORT).show()
        }
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
            onNavigateToSettings = onNavigateToSettings,
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
                text = stringResource(id = R.string.share_your_feedback),
                color = colorResource(id = R.color.text_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.help_us_improve_marimo_care_choose_a_topic_and_send_us_your_thoughts_ideas_or_bug_reports),
                color = colorResource(id = R.color.text_body),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Card 1: Choose a topic
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, colorResource(id = R.color.marimo_border))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string._1_choose_a_topic),
                        color = colorResource(id = R.color.text_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Topic Grid (2 Columns)
                    val topics = listOf(
                        Pair(bugReportText, R.drawable.ic_topic_bug),
                        Pair(featureRequestText, R.drawable.ic_topic_feature),
                        Pair(uiDesignText, R.drawable.ic_topic_ui),
                        Pair(newContentText, R.drawable.ic_topic_content),
                        Pair(generalFeedbackText, R.drawable.ic_topic_feedback),
                        Pair(otherText, R.drawable.ic_topic_other)
                    )

                    Column {
                        topics.chunked(2).forEach { rowTopics ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowTopics.forEach { (topic, icon) ->
                                    val isSelected = selectedTopics.contains(topic)
                                    TopicButton(
                                        text = topic,
                                        iconRes = icon,
                                        isSelected = isSelected,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (isSelected) {
                                                selectedTopics.remove(topic)
                                            } else {
                                                selectedTopics.add(topic)
                                            }
                                        }
                                    )
                                }
                                if (rowTopics.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Card 2: Write your message
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, colorResource(id = R.color.marimo_border))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string._2_write_your_message),
                            color = colorResource(id = R.color.text_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                selectedTopics.clear()
                                messageText = ""
                                Toast.makeText(context, context.getString(R.string.form_cleared), Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clear),
                                contentDescription = stringResource(id = R.string.clear_all),
                                tint = colorResource(id = R.color.marimo_red)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.describe_your_idea_issue_or_suggestion_here),
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.marimo_outline),
                            focusedBorderColor = colorResource(id = R.color.marimo_primary)
                        )
                    )

                    Button(
                        onClick = {
                            val trimmedMessage = messageText.trim()
                            if (selectedTopics.isEmpty() || trimmedMessage.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.please_select_at_least_one_topic_and_write_a_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val emailSubject = context.getString(R.string.marimo_care_feedback, selectedTopics.joinToString(", "))
                                sendEmail(emailSubject, trimmedMessage)

                                coroutineScope.launch(Dispatchers.IO) {
                                    AchievementManager.recalculateAll(
                                        true,
                                        mapOf("submitted_feedback" to true),
                                        context
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.marimo_button_feedback))
                    ) {
                        Text(text = stringResource(id = R.string.send_feedback), color = Color.White)
                    }
                }
            }

            // Card 3: Join Community
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.card_background)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, colorResource(id = R.color.marimo_border))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.join_the_marimo_community),
                        color = colorResource(id = R.color.text_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SocialButton(
                            text = stringResource(id = R.string.reddit),
                            bgColor = colorResource(id = R.color.social_reddit),
                            textColor = Color.White,
                            modifier = Modifier.weight(1f),
                            onClick = { openWebPage(context.getString(R.string.https_www_reddit_com_r_marimo)) }
                        )
                        SocialButton(
                            text = stringResource(id = R.string.whatsapp),
                            bgColor = colorResource(id = R.color.social_whatsapp_bg),
                            textColor = colorResource(id = R.color.social_whatsapp_text),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val appStoreLink = context.getString(R.string.https_play_google_com_store_apps_details_id_rpt_tool_marimocare)
                                context.shareViaWhatsApp(context.getString(R.string.check_out_marimo_care, appStoreLink))
                            }
                        )
                        SocialButton(
                            text = stringResource(id = R.string.instagram),
                            bgColor = colorResource(id = R.color.social_instagram),
                            textColor = Color.White,
                            modifier = Modifier.weight(1f),
                            onClick = { openWebPage(context.getString(R.string.https_www_instagram_com_explore_tags_marimo)) }
                        )
                    }
                }
            }

            Footer()
        }
    }
}

@Composable
fun TopicButton(
    text: String,
    iconRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) colorResource(id = R.color.marimo_bg_mint_selected) else colorResource(id = R.color.card_background)
    val borderColor = if (isSelected) colorResource(id = R.color.marimo_primary) else colorResource(id = R.color.marimo_outline)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = colorResource(id = R.color.marimo_text_primary),
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = colorResource(id = R.color.text_title),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SocialButton(
    text: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Header(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAddMarimo: () -> Unit,
    onNavigateToAchievement: () -> Unit,
    onNavigateToSettings: () -> Unit,
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
            HeaderIconButton(iconRes = R.drawable.ic_settings, colorRes = R.color.marimo_add_icon, text = stringResource(id = R.string.settings), onClick = onNavigateToSettings)
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