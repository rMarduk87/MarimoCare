package rpt.tool.marimocare.utils.data.repositories

import rpt.tool.marimocare.utils.data.appmodels.ChatHistoryItem
import rpt.tool.marimocare.utils.data.database.dao.ChatHistoryDao

class ChatHistoryRepository(
    private val chatHistoryDao: ChatHistoryDao
) {
    suspend fun addChatHistoryItem(role: String, content: String, timestamp: Long) {
        val nextId = chatHistoryDao.getLastId() + 1
        val item = ChatHistoryItem(nextId, role, content, timestamp)
        chatHistoryDao.insert(item.toDBModel())
    }

    suspend fun getChatHistory(): List<ChatHistoryItem> {
        return chatHistoryDao.getAll().map { it.map() }
    }

    suspend fun clearHistory() {
        chatHistoryDao.clear()
    }
}
