package rpt.tool.marimocare.utils.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel

@Dao
interface MarimoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(marimo: MarimoModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(marimos: List<MarimoModel>)

    @Query("DELETE FROM marimo")
    fun clear()

    @Query("SELECT max(code) FROM marimo")
    fun getLastId() : Int

    @Transaction
    @Query("SELECT * FROM marimo ORDER BY code COLLATE NOCASE ASC")
    fun getMarimos(): LiveData<List<MarimoModel>>

    @Transaction
    @Query("SELECT * FROM marimo WHERE code = :code")
    fun getMarimo(code: Int): MarimoModel

    @Transaction
    @Query("UPDATE marimo SET name = :name, frequency_changes = :freq, " +
            "last_water_changes = :lastWater, notes = :notes WHERE code = :code")
    fun update(
        code: Int,
        name: String,
        freq: Int,
        lastWater: String,
        notes: String
    )

    @Transaction
    @Query("UPDATE marimo SET last_water_changes = :lastChanged WHERE code = :code")
    fun updateWater(lastChanged: String, code: Int)

    @Transaction
    @Query("SELECT * FROM marimo ORDER BY code COLLATE NOCASE ASC")
    fun getAll(): List<MarimoModel>

    @Transaction
    @Query("SELECT * FROM marimo WHERE frequency_changes = ( SELECT MIN(frequency_changes) " +
            "FROM marimo ) order by name ASC;")
    fun getMarimoMostFrequentChanged() : List<MarimoModel>

    @Transaction
    @Query("SELECT * FROM marimo WHERE frequency_changes = ( SELECT MAX(frequency_changes) " +
            "FROM marimo ) order by name ASC;")
    fun getMarimoLastFrequentChanged() : List<MarimoModel>

    @Query("SELECT max(code) FROM marimo_changes")
    fun getLastIdFromWaterChanges() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWaterChanges(marimo: MarimoChangeModel)

    @Transaction
    @Query("SELECT count(*) FROM marimo_changes")
    fun getTotalWaterChanges() : Int

    @Transaction
    @Query("SELECT * FROM marimo_changes ORDER BY code COLLATE NOCASE ASC")
    fun getAllWaterChanges(): List<MarimoChangeModel>


}