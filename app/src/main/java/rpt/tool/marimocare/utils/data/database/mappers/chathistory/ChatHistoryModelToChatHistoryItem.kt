package rpt.tool.marimocare.utils.data.database.mappers.chathistory

import rpt.tool.marimocare.utils.data.appmodels.ChatHistoryItem
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.ChatHistoryModel

class ChatHistoryModelToChatHistoryItem : ModelMapper<ChatHistoryModel, ChatHistoryItem> {
    override val destination: Class<ChatHistoryItem> = ChatHistoryItem::class.java

    override fun map(source: ChatHistoryModel): ChatHistoryItem {
        return ChatHistoryItem(
            id = source.id,
            role = source.role,
            content = source.content,
            timestamp = source.timestamp
        )
    }
}
