package rpt.tool.marimocare.utils.view.recyclerview.items.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.ItemMessageReceivedBinding
import rpt.tool.marimocare.utils.data.appmodels.ChatHistoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatReceivedItem(val chatItem: ChatHistoryItem) :
    AbstractBindingItem<ItemMessageReceivedBinding>() {

    override val type: Int
        get() = R.id.chat_received_item_id

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?):
            ItemMessageReceivedBinding {
        return ItemMessageReceivedBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemMessageReceivedBinding, payloads: List<Any>) {
        binding.tvMessage.text = chatItem.content
        binding.tvTimestamp.text =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(chatItem.timestamp))
    }
}
