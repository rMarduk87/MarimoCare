package rpt.tool.marimocare.utils.data.database.dao.decoration

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import rpt.tool.marimocare.utils.data.database.models.decoration.PotDecorationModel

@Dao
interface PotDecorationDao {
    @Query("SELECT * FROM pot_decoration WHERE marimo_code = :marimoCode")
    suspend fun getDecorationsForMarimo(marimoCode: Int): List<PotDecorationModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(decorations: List<PotDecorationModel>)

    @Query("DELETE FROM pot_decoration WHERE marimo_code = :marimoCode")
    suspend fun deleteDecorationsForMarimo(marimoCode: Int)

    @Query("SELECT * FROM pot_decoration")
    suspend fun getAllDecorations(): List<PotDecorationModel>

    @Transaction
    @Query("SELECT * FROM pot_decoration ORDER BY id COLLATE NOCASE ASC")
    fun getDecorations() : LiveData<List<PotDecorationModel>>
}