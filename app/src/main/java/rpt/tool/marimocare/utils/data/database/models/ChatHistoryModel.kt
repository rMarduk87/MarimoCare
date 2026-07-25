package rpt.tool.marimocare.utils.data.database.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import rpt.tool.marimocare.utils.data.DbModel
import rpt.tool.marimocare.utils.data.database.mappers.addMapper
import rpt.tool.marimocare.utils.data.database.mappers.chathistory.ChatHistoryModelToChatHistoryItem

@Keep
@Entity(tableName = "chat_history")
class ChatHistoryModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
) : DbModel() {

    init {
        addMapper(ChatHistoryModelToChatHistoryItem())
    }
}
