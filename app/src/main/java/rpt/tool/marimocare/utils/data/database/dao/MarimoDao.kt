package rpt.tool.marimocare.utils.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import rpt.tool.marimocare.utils.data.database.models.MarimoModel

@Dao
interface MarimoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(marimo: MarimoModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(marimos: List<MarimoModel>)

    @Query("DELETE FROM marimo")
    fun clear()

    @Transaction
    @Query("SELECT * FROM marimo ORDER BY code COLLATE NOCASE ASC")
    fun getMarimos(): LiveData<List<MarimoModel>>


}