package rpt.tool.marimocare.ui.feedback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentFeedbackBinding
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import androidx.core.net.toUri

class FeedbackFragment :
    BaseFragment<FragmentFeedbackBinding>(FragmentFeedbackBinding::inflate) {

    private val selectedTopics = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeaderButtons()

        binding.include1.appLogo.setOnClickListener {
            safeNavController?.safeNavigate(
                FeedbackFragmentDirections
                    .actionFeedbackFragmentToDashboardFragment())
        }

        val topics = listOf(
            Pair(binding.topicBug, binding.tvTopicBug),
            Pair(binding.topicFeature, binding.tvTopicFeature),
            Pair(binding.topicUI, binding.tvTopicUI),
            Pair(binding.topicContent, binding.tvTopicContent),
            Pair(binding.topicGeneral, binding.tvTopicGeneral),
            Pair(binding.topicOther, binding.tvTopicOther)
        )

        topics.forEach { (layout, textView) ->
            layout.setOnClickListener {
                val topicText = textView.text.toString()

                if (selectedTopics.contains(topicText)) {
                    selectedTopics.remove(topicText)
                    layout.setBackgroundResource(R.drawable.bg_button_outlined)
                } else {
                    selectedTopics.add(topicText)
                    layout.setBackgroundResource(R.drawable.bg_button_selected)
                }
            }
        }

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()

            if (selectedTopics.isEmpty() || messageText.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_at_least_one_topic_and_write_a_message),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val emailSubject =
                getString(R.string.marimo_care_feedback, selectedTopics.joinToString(", "))
            sendEmail(emailSubject, messageText)
        }

        binding.btnReddit.setOnClickListener {
            openWebPage(getString(R.string.https_www_reddit_com_r_marimo))
        }

        binding.btnInstagram.setOnClickListener {
            openWebPage(getString(R.string.https_www_instagram_com_explore_tags_marimo))
        }

        binding.btnWhatsapp.setOnClickListener {
            val appStoreLink =
                getString(R.string.https_play_google_com_store_apps_details_id_rpt_tool_marimocare)
            shareViaWhatsApp(getString(R.string.check_out_marimo_care, appStoreLink))
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

    private fun sendEmail(subject: String, message: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(
                R.string.riccardo_pezzolati_gmail_com)))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            startActivity(Intent.createChooser(emailIntent,
                getString(R.string.choose_email_client)))
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                getString(R.string.no_email_app_found_on_your_device),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWebPage(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                getString(R.string.unable_to_open_link),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareViaWhatsApp(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                getString(R.string.whatsapp_is_not_installed_on_this_device),
                Toast.LENGTH_SHORT).show()
        }
    }
}