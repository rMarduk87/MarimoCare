package rpt.tool.marimocare.ui.chat

import android.app.AlertDialog
import android.widget.PopupMenu
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.com.base.BaseFragment
import rpt.com.base.navigation.safeNavController
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentChatBinding
import rpt.tool.marimocare.utils.data.appmodels.ChatHistoryItem
import rpt.tool.marimocare.utils.managers.QuestionAnswersManager
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import rpt.tool.marimocare.utils.view.gone
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.recyclerview.items.chat.ChatReceivedItem
import rpt.tool.marimocare.utils.view.recyclerview.items.chat.ChatSentItem
import rpt.tool.marimocare.utils.view.shareViaWhatsApp
import rpt.tool.marimocare.utils.view.visible

class QuestionsAnswersChatFragment :
    BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate, true) {

    private val itemAdapter = ItemAdapter<com.mikepenz.fastadapter.IItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private lateinit var qaManager: QuestionAnswersManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qaManager = QuestionAnswersManager(requireContext())

        setupHeader()
        setupRecyclerView()
        setupListeners()
        loadHistory()
        checkLanguageChange()
        setupWindowInsets()

        binding.includeHeader.appLogo.setOnClickListener {
            safeNavController(R.id.main_activity_nav_host_fragment)?.popBackStack()
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.inputLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            
            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom
            v.updatePadding(bottom = bottomPadding)

            if (ime.bottom > 0) {
                binding.recyclerChat.post {
                    if (itemAdapter.adapterItemCount > 0) {
                        binding.recyclerChat.scrollToPosition(itemAdapter.adapterItemCount - 1)
                    }
                }
            }

            insets
        }
    }

    private fun setupHeader() {
        HeaderHelper.setupHeaderButtons(
            requireContext(),
            listOf(
                HeaderButtonConfig(
                    button = binding.includeHeader.btnDashboardHeader,
                    iconRes = R.drawable.ic_dashboard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.dashboard),
                    onClick = {
                        safeNavController(R.id.main_activity_nav_host_fragment)?.popBackStack()
                    }
                ),
                HeaderButtonConfig(
                    button = binding.includeHeader.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = {
                        safeNavController(R.id.main_activity_nav_host_fragment)?.popBackStack()
                    }
                ),
                HeaderButtonConfig(
                    button = binding.includeHeader.btnAchievementAHeader,
                    iconRes = R.drawable.ic_coccard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.achievement),
                    onClick = {
                        safeNavController(R.id.main_activity_nav_host_fragment)?.popBackStack()
                    }
                ),
                HeaderButtonConfig(
                    button = binding.includeHeader.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = {
                        safeNavController(R.id.main_activity_nav_host_fragment)?.popBackStack()
                    }
                ),
                HeaderButtonConfig(
                    button = binding.includeHeader.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    onClick = {
                        safeNavController(R.id.main_activity_nav_host_fragment)?.popBackStack()
                    }
                )
            )
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = fastAdapter
        }

        fastAdapter.onLongClickListener = { view, _, item, _ ->
            if (item is ChatReceivedItem) {
                showForwardMenu(view, item.chatItem.content)
                true
            } else {
                false
            }
        }
    }

    private fun showForwardMenu(view: View, message: String) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(requireContext().getString(R.string.forward_to_whatsapp))
        popup.setOnMenuItemClickListener {
            requireContext().shareViaWhatsApp(message)
            true
        }
        popup.show()
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            }
        }

        binding.btnClear.setOnClickListener {
            clearConversation()
        }
    }

    private fun checkLanguageChange() {
        val currentLanguage = resources.configuration.locales.get(0).language
        val savedLanguage = SharedPreferencesManager.appLanguage

        if (savedLanguage.isNotEmpty() && savedLanguage != currentLanguage) {
            clearConversation()
        }
        SharedPreferencesManager.appLanguage = currentLanguage
    }

    private fun clearConversation() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_clear_conversation, null)

        val dialog = AlertDialog.Builder(requireContext(),
            R.style.CustomDialogTheme)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnClose = dialogView.findViewById<View>(R.id.btnClose)
        val btnCancel = dialogView.findViewById<View>(R.id.btnCancel)
        val btnConfirmClear = dialogView.findViewById<View>(R.id.btnConfirmClear)

        val dismissListener = View.OnClickListener { dialog.dismiss() }
        btnClose.setOnClickListener(dismissListener)
        btnCancel.setOnClickListener(dismissListener)

        btnConfirmClear.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                RepositoryManager.chatHistoryRepository.clearHistory()
                qaManager.clearConversation()
                withContext(Dispatchers.Main) {
                    itemAdapter.clear()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window?.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun sendMessage(text: String) {
        binding.etMessage.text?.clear()
        val timestamp = System.currentTimeMillis()
        
        lifecycleScope.launch(Dispatchers.IO) {
            val userItem = ChatHistoryItem(0, "user", text, timestamp)
            RepositoryManager.chatHistoryRepository.addChatHistoryItem("user", text,
                timestamp)
            
            withContext(Dispatchers.Main) {
                itemAdapter.add(ChatSentItem(userItem))
                binding.recyclerChat.smoothScrollToPosition(itemAdapter.adapterItemCount - 1)
                showLoading(true)
            }

            val response = qaManager.putQuestion(text)
            val responseTimestamp = System.currentTimeMillis()
            val aiItem = ChatHistoryItem(0, "assistant",
                response, responseTimestamp)
            RepositoryManager.chatHistoryRepository.addChatHistoryItem("assistant",
                response, responseTimestamp)

            withContext(Dispatchers.Main) {
                showLoading(false)
                itemAdapter.add(ChatReceivedItem(aiItem))
                binding.recyclerChat.smoothScrollToPosition(itemAdapter.adapterItemCount - 1)
            }
        }
    }

    private fun loadHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val history = RepositoryManager.chatHistoryRepository.getChatHistory()
            val items = history.map {
                if (it.role == "user") ChatSentItem(it) else ChatReceivedItem(
                    it)
            }
            withContext(Dispatchers.Main) {
                itemAdapter.set(items)
                if (itemAdapter.adapterItemCount > 0) {
                    binding.recyclerChat.scrollToPosition(itemAdapter.adapterItemCount - 1)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.loadingBar.visible()
            binding.btnSend.isEnabled = false
        } else {
            binding.loadingBar.gone()
            binding.btnSend.isEnabled = true
        }
    }
}
