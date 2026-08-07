package rpt.tool.marimocare.utils.data.database.dao

import androidx.room.*
import rpt.tool.marimocare.utils.data.database.models.ChatHistoryModel

@Dao
interface ChatHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChatHistoryModel)

    @Query("SELECT * FROM chat_history ORDER BY id ASC")
    suspend fun getAll(): List<ChatHistoryModel>

    @Query("SELECT max(id) FROM chat_history")
    suspend fun getLastId(): Int

    @Query("DELETE FROM chat_history")
    suspend fun clear()
}
