package rpt.tool.marimocare.utils.data.database.mappers.chathistory

import rpt.tool.marimocare.utils.data.appmodels.ChatHistoryItem
import rpt.tool.marimocare.utils.data.database.mappers.ModelMapper
import rpt.tool.marimocare.utils.data.database.models.ChatHistoryModel

class ChatHistoryItemToChatHistoryModel : ModelMapper<ChatHistoryItem, ChatHistoryModel> {
    override val destination: Class<ChatHistoryModel> = ChatHistoryModel::class.java

    override fun map(source: ChatHistoryItem): ChatHistoryModel {
        return ChatHistoryModel(
            id = source.id,
            role = source.role,
            content = source.content,
            timestamp = source.timestamp
        )
    }
}
