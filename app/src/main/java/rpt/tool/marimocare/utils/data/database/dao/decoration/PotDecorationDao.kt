package rpt.tool.marimocare.utils.data.database.dao.decoration

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import rpt.tool.marimocare.utils.data.database.models.decoration.PotDecorationModel

@Dao
interface PotDecorationDao {
    @Query("SELECT * FROM pot_decoration WHERE marimo_code = :marimoCode")
    fun getDecorationsForMarimo(marimoCode: Int): List<PotDecorationModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(decorations: List<PotDecorationModel>)

    @Query("DELETE FROM pot_decoration WHERE marimo_code = :marimoCode")
    fun deleteDecorationsForMarimo(marimoCode: Int)
}