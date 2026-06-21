package rpt.tool.marimocare.utils.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScoreStats
import rpt.tool.marimocare.utils.data.database.models.AchievementDetailModel
import rpt.tool.marimocare.utils.data.database.models.AchievementModel
import rpt.tool.marimocare.utils.data.database.models.MarimoChangeModel
import rpt.tool.marimocare.utils.data.database.models.MarimoHealthScoreModel
import rpt.tool.marimocare.utils.data.database.models.MarimoModel
import rpt.tool.marimocare.utils.data.database.models.MarimoQRModel
import rpt.tool.marimocare.utils.data.database.models.complex.AchievementWithDetailModel

@Dao
interface MarimoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marimo: MarimoModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bulkInsert(marimos: List<MarimoModel>)

    @Query("DELETE FROM marimo")
    suspend fun clear()

    @Query("SELECT max(code) FROM marimo")
    suspend fun getLastId() : Int

    @Transaction
    @Query("SELECT * FROM marimo ORDER BY code COLLATE NOCASE ASC")
    fun getMarimos(): LiveData<List<MarimoModel>>

    @Transaction
    @Query("SELECT * FROM marimo WHERE code = :code")
    suspend fun getMarimo(code: Int): MarimoModel

    @Transaction
    @Query("UPDATE marimo SET name = :name, frequency_changes = :freq, " +
            "last_water_changes = :lastWater, notes = :notes, photo = :photo, registration_date" +
            " = :registrationDate WHERE code = :code")
    suspend fun update(
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
    suspend fun updateWater(lastChanged: String, code: Int)

    @Transaction
    @Query("SELECT * FROM marimo ORDER BY code COLLATE NOCASE ASC")
    suspend fun getAll(): List<MarimoModel>

    @Transaction
    @Query("SELECT * FROM marimo WHERE frequency_changes = ( SELECT MIN(frequency_changes) " +
            "FROM marimo ) order by name ASC;")
    suspend fun getMarimoMostFrequentChanged() : List<MarimoModel>

    @Transaction
    @Query("SELECT * FROM marimo WHERE frequency_changes = ( SELECT MAX(frequency_changes) " +
            "FROM marimo ) order by name ASC;")
    suspend fun getMarimoLastFrequentChanged() : List<MarimoModel>

    @Query("SELECT max(code) FROM marimo_changes")
    suspend fun getLastIdFromWaterChanges() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterChanges(marimo: MarimoChangeModel)

    @Transaction
    @Query("SELECT count(*) FROM marimo_changes")
    suspend fun getTotalWaterChanges() : Int

    @Transaction
    @Query("SELECT avg(health) FROM marimo_health_score where date =:currentDate")
    suspend fun getAverageHealth(currentDate: String): Int

    @Transaction
    @Query("SELECT * FROM marimo_changes ORDER BY code COLLATE NOCASE ASC")
    suspend fun getAllWaterChanges(): List<MarimoChangeModel>

    @Transaction
    @Query("DELETE FROM marimo WHERE code = :code")
    suspend fun delete(code: Int)

    @Transaction
    @Query("SELECT * FROM marimo_qr WHERE marimo_code = :marimoCode")
    suspend fun getMarimoQR(marimoCode: Int) : MarimoQRModel?

    @Transaction
    @Query("UPDATE marimo_qr SET validity = :bool WHERE marimo_code = :marimoCode")
    suspend fun invalidateMarimoQR(marimoCode: Int, bool: Boolean)

    @Query("SELECT max(code) FROM marimo_qr")
    suspend fun getLastQrId() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQr(marimoQr: MarimoQRModel)

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
    suspend fun getMarimoHealthScores(currentDate : String): List<MarimoHealthScoreStats>

    @Transaction
    @Query("SELECT * FROM marimo where registration_date is null ORDER BY code COLLATE NOCASE ASC")
    suspend fun getAllWithoutRegistration(): List<MarimoModel>

    @Transaction
    @Query("SELECT count(*) FROM marimo_changes where marimo_code = :marimoCode")
    suspend fun getMarimoTotalWaterChanged(marimoCode: Int): Int

    @Transaction
    @Query("SELECT count(is_milestone) FROM marimo_changes where marimo_code = :marimoCode" +
            " AND is_milestone = 1")
    suspend fun getMarimoTotalMilestones(marimoCode: Int): Int

    @Transaction
    @Query("SELECT * FROM marimo_changes where marimo_code = :marimoCode")
    suspend fun getAllChanges(marimoCode: Int):List<MarimoChangeModel>

    @Transaction
    @Query("SELECT max(code) FROM marimo_health_score")
    suspend fun getLastIdFromMarimoHealth(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewHealth(health: MarimoHealthScoreModel)

    @Transaction
    @Query("SELECT health FROM marimo_health_score where " +
            "marimo_code =:marimoCode and date =:currentDate")
    suspend fun getCurrentHealth(marimoCode: Int, currentDate: String): Int

    @Transaction
    @Query("SELECT count(*) FROM marimo_health_score where " +
            "marimo_code =:marimoCode and date =:currentDate")
    suspend fun healthExists(marimoCode: Int, currentDate: String): Int

    @Transaction
    @Query("SELECT date FROM marimo_health_score where " +
            "marimo_code =:code order by code desc limit 1")
    suspend fun getLastHealthDate(code: Int): String?

    @Transaction
    @Query("UPDATE marimo_health_score SET health = :health where " +
            "marimo_code =:marimoCoe and date =:last")
    suspend fun updateHealth(marimoCoe: Int, last: String, health: Int)

    @Transaction
    @Query("SELECT * FROM marimo_health_score where marimo_code =:marimoCode order by date desc")
    suspend fun getAllHealth(marimoCode: Int): List<MarimoHealthScoreModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementModel>)

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 1 ORDER BY `order` ASC")
    suspend fun getEarnedAchievements(): List<AchievementModel>

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 0 ORDER BY `order` ASC")
    suspend fun getLockedAchievements(): List<AchievementModel>

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 1 ORDER BY `order` ASC")
    suspend fun getEarnedAchievementsWithDetail(): List<AchievementWithDetailModel>

    @Transaction
    @Query("SELECT * FROM achievement WHERE earned = 0 ORDER BY `order` ASC")
    suspend fun getLockedAchievementsWithDetail(): List<AchievementWithDetailModel>

    @Transaction
    @Query("UPDATE achievement SET earned = 0, acquired_date = NULL")
    suspend fun resetAllAchievements()

    @Transaction
    @Query("UPDATE achievement SET earned = 1, acquired_date = :date where id = :id")
    suspend fun earnAchievement(id: Int, date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievementDetails(details: List<AchievementDetailModel>)

    @Transaction
    @Query("SELECT * FROM achievement_details WHERE achievement_id = :achievementId")
    suspend fun getAchievementDetails(achievementId: Int): List<AchievementDetailModel>

    @Transaction
    @Query("DELETE FROM achievement_details")
    suspend fun clearAchievementDetails()

    @Transaction
    @Query("SELECT * FROM achievement ORDER BY `order` ASC")
    suspend fun getAllAchievement() : List<AchievementWithDetailModel>

    @Transaction
    @Query("UPDATE achievement_details set `current` = :current where achievement_id =:id")
    suspend fun updateAchievementDetail(id: Int, current: Int)

    @Transaction
    @Query("SELECT * FROM achievement_details where achievement_id =:id ")
    suspend fun getAchievementDetail(id: Int): AchievementDetailModel
}