package rpt.tool.marimocare.utils.view.recyclerview.items.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMessageSentBinding
import rpt.tool.marimocare.utils.data.appmodels.ChatHistoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatSentItem(val chatItem: ChatHistoryItem) : AbstractBindingItem<ItemMessageSentBinding>() {

    override val type: Int
        get() = R.id.chat_sent_item_id

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?):
            ItemMessageSentBinding {
        return ItemMessageSentBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemMessageSentBinding, payloads: List<Any>) {
        binding.tvMessage.text = chatItem.content
        binding.tvTimestamp.text = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(chatItem.timestamp))
    }
}
