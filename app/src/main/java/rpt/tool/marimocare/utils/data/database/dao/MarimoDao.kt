package rpt.tool.marimocare.utils.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScoreStats
import rpt.tool.marimocare.utils.data.database.models.AchievementModel
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel
import rpt.tool.marimocare.utils.data.database.models.MarimoHealthScoreModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel
import rpt.tool.marimocare.utils.data.database.models.MarimoQRModel

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
            "last_water_changes = :lastWater, notes = :notes, photo = :photo, registration_date" +
            " = :registrationDate WHERE code = :code")
    fun update(
        code: Int,
        name: String,
        freq: Int,
        lastWater: String,
        notes: String,
        photo: String?,
        registrationDate: String
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
    @Query("SELECT avg(health) FROM marimo_health_score where date =:currentDate")
    fun getAverageHealth(currentDate: String): Int

    @Transaction
    @Query("SELECT * FROM marimo_changes ORDER BY code COLLATE NOCASE ASC")
    fun getAllWaterChanges(): List<MarimoChangeModel>

    @Transaction
    @Query("DELETE FROM marimo WHERE code = :code")
    fun delete(code: Int)

    @Transaction
    @Query("SELECT * FROM marimo_qr WHERE marimo_code = :marimoCode")
    fun getMarimoQR(marimoCode: Int) : MarimoQRModel?

    @Transaction
    @Query("UPDATE marimo_qr SET validity = :bool WHERE marimo_code = :marimoCode")
    fun invalidateMarimoQR(marimoCode: Int, bool: Boolean)

    @Query("SELECT max(code) FROM marimo_qr")
    fun getLastQrId() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQr(marimoQr: MarimoQRModel)

    @Query("""
    SELECT 
        m.code AS id,
        m.name AS name,
        m.frequency_changes AS frequency,
        mhs.health AS health,
        COUNT(c.code) AS totalChanges
    FROM marimo m
    LEFT JOIN marimo_changes c 
        ON m.code = c.marimo_code
     LEFT JOIN marimo_health_score mhs
     ON m.code = mhs.marimo_code
     where mhs.date = :currentDate
    GROUP BY m.code
    ORDER BY m.code ASC
    """)
    fun getMarimoHealthScores(currentDate : String): List<MarimoHealthScoreStats>

    @Transaction
    @Query("SELECT * FROM marimo where registration_date is null ORDER BY code COLLATE NOCASE ASC")
    fun getAllWithoutRegistration(): List<MarimoModel>

    @Transaction
    @Query("SELECT count(*) FROM marimo_changes where marimo_code = :marimoCode")
    fun getMarimoTotalWaterChanged(marimoCode: Int): Int

    @Transaction
    @Query("SELECT count(is_milestone) FROM marimo_changes where marimo_code = :marimoCode" +
            " AND is_milestone = 1")
    fun getMarimoTotalMilestones(marimoCode: Int): Int

    @Transaction
    @Query("SELECT * FROM marimo_changes where marimo_code = :marimoCode")
    fun getAllChanges(marimoCode: Int):List<MarimoChangeModel>

    @Transaction
    @Query("SELECT max(code) FROM marimo_health_score")
    fun getLastIdFromMarimoHealth(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewHealth(health: MarimoHealthScoreModel)

    @Transaction
    @Query("SELECT health FROM marimo_health_score where " +
            "marimo_code =:marimoCode and date =:currentDate")
    fun getCurrentHealth(marimoCode: Int, currentDate: String): Int

    @Transaction
    @Query("SELECT count(*) FROM marimo_health_score where " +
            "marimo_code =:marimoCode and date =:currentDate")
    fun healthExists(marimoCode: Int, currentDate: String): Int

    @Transaction
    @Query("SELECT date FROM marimo_health_score where " +
            "marimo_code =:code order by code desc limit 1")
    fun getLastHealthDate(code: Int): String?

    @Transaction
    @Query("UPDATE marimo_health_score SET health = :health where " +
            "marimo_code =:marimoCoe and date =:last")
    fun updateHealth(marimoCoe: Int, last: String, health: Int)

    @Transaction
    @Query("SELECT * FROM marimo_health_score where marimo_code =:marimoCode order by date desc")
    fun getAllHealth(marimoCode: Int): List<MarimoHealthScoreModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAchievements(achievements: List<AchievementModel>)

}