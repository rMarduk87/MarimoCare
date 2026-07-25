package rpt.tool.marimocare.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.marimocare.utils.data.AppModel
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.chathistory.ChatHistoryItemToChatHistoryModel
import rpt.tool.marimocare.utils.data.database.models.ChatHistoryModel
import java.io.Serializable

@Keep
data class ChatHistoryItem(
    val id: Int,
    val role: String,
    val content: String,
    val timestamp: Long
) : AppModel(), Serializable {

    init {
        addMapper(ChatHistoryItemToChatHistoryModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == ChatHistoryModel::class.java }.map(this) as T
    }
}
