package rpt.tool.marimocare.utils.data.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Achievement
import rpt.tool.marimocare.utils.data.appmodels.AchievementComplex
import rpt.tool.marimocare.utils.data.appmodels.AchievementDetail
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScore
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScoreStats
import rpt.tool.marimocare.utils.data.appmodels.MarimoQR
import rpt.tool.marimocare.utils.data.database.enums.AchievementType
import rpt.tool.marimocare.utils.data.database.enums.UnitType
import rpt.tool.marimocare.utils.data.database.dao.MarimoDao
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.Int
import kotlin.collections.map

private fun String.cleanValue(): String = this.trim().removeSurrounding("\"")

class MarimoRepository(
    private val marimoDao: MarimoDao
) {
    fun clearAll() {
        marimoDao.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addMarimo(
        marimoName: String, lastWaterChange: String, notes: String, freq: Int,
        photo: String?,
        registrationDate: String
    ) : Int {
        Marimo(marimoDao.getLastId()+1,marimoName, freq,
            lastWaterChange, AppUtils.nextChange(
            lastWaterChange,
            freq), notes, AppUtils.daysUntil(
            AppUtils.nextChange(
                lastWaterChange,
                freq)),photo,registrationDate).let {

            marimoDao.insert(it.map())
        }

        return marimoDao.getLastId()
    }

    fun getMarimo(marimoCode: Int): Marimo? {
        return marimoDao.getMarimo(marimoCode).map()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMarimo(
        code: Int, name: String, lastWater: String, notes: String, freq: Int,
        photo: String?,
        registrationDate: String
    ) {
        marimoDao.update(code,name, freq,
                lastWater, notes,photo, registrationDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMarimo(marimo: Marimo){
        marimo.lastChanged?.let {
            marimo.notes?.let { notes ->
                marimo.registrationDate?.let { registrationDate ->
                    updateMarimo(marimo.code,marimo.name,it,
                        notes,marimo.changeFrequencyDays,
                        marimo.photo, registrationDate)
                }
            }
        }
    }

    fun updateWaterMarimo(lastChanged: String, code: Int) {
        marimoDao.updateWater(lastChanged, code)
    }

    fun getAllSync(check: Boolean = false):List<Marimo> {
        if(!check){
            return marimoDao.getAll().map { it.map() }
        }
        return marimoDao.getAllWithoutRegistration().map { it.map() }
    }

    fun getAverageFrequency():Int {
        var sum = 0
        marimoDao.getAll().forEach {
            sum += it.frequencyChanges
        }

        return sum/if(marimoDao.getAll().isNotEmpty()) marimoDao.getAll().size else 1

    }

    fun getMarimoMostFrequentChanged():List<Marimo> {
        return marimoDao.getMarimoMostFrequentChanged().map() { it.map() }
    }

    fun getMarimoLastFrequentChanged():List<Marimo> {
        return marimoDao.getMarimoLastFrequentChanged().map() { it.map() }
    }

    fun addWaterChanges(id: Int, lastWater: String, waterChangeNotes: String?, imagePath: String?
                        ,isMilestone: Boolean = false) {
        MarimoChange(marimoDao.getLastIdFromWaterChanges()+1,id.toString(),
            lastWater,waterChangeNotes,
            imagePath,isMilestone).let {

            marimoDao.insertWaterChanges(it.map())
        }
    }

    fun getTotalWaterChanged() : Int {
        return marimoDao.getTotalWaterChanges()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAverageHealth() : Int {
        return marimoDao.getAverageHealth(AppUtils.getCurrentDate())
    }

    fun getAllChanges(): List<MarimoChange> {
        return marimoDao.getAllWaterChanges().map { it.map() }
    }

    fun deleteMarimo(code: Int) {
        marimoDao.delete(code)
    }

    fun addMarimoQR(marimoCode: Int, qrCodeToStore: String) {
        val existingQR = marimoDao.getMarimoQR(marimoCode)
        if (existingQR != null) {
            marimoDao.invalidateMarimoQR(marimoCode, false)
        }
        MarimoQR(marimoDao.getLastQrId() + 1, marimoCode, qrCodeToStore, true).let {
            marimoDao.insertQr(it.map())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHealthScore() : List<MarimoHealthScoreStats> {
        return marimoDao.getMarimoHealthScores(AppUtils.getCurrentDate())
    }

    fun getMarimoTotalWaterChanged(marimoCode: Int): Int {
        return marimoDao.getMarimoTotalWaterChanged(marimoCode)
    }

    fun getMarimoTotalMilestones(marimoCode: Int): Int  {
        return marimoDao.getMarimoTotalMilestones(marimoCode)
    }

    fun getAllChanges(marimoCode: Int):List<MarimoChange> {
        return marimoDao.getAllChanges(marimoCode).map { it.map() }

    }

    fun addMarimoHealthScore(id: Int, currentDate: String, health: Int) {
        MarimoHealthScore(marimoDao.getLastIdFromMarimoHealth()+1, id, currentDate,
            health).let{
            marimoDao.insertNewHealth(it.map())
        }
    }

    fun updateHealthScore(marimoCoe: Int, last: String, health: Int) {
        marimoDao.updateHealth(marimoCoe, last, health)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSpecificHealth(marimoCode: Int, last: String?): Int {
        return marimoDao.getCurrentHealth(marimoCode, last ?: AppUtils.getCurrentDate())
    }

    fun healthExists(marimoCode: Int, last: String) : Int {
        return marimoDao.healthExists(marimoCode, last)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLastHealthDate(code: Int) : String {
        val date = marimoDao.getLastHealthDate(code)
        return date ?: AppUtils.getCurrentDate()
    }

    fun getAllHealth(marimoCode: Int): List<MarimoHealthScore> {
        return marimoDao.getAllHealth(marimoCode).map { it.map() }
    }


    val marimos: LiveData<List<Marimo>> =
        marimoDao.getMarimos().map { it.map { it.map() } }

    fun getEarnedAchievements(): List<AchievementComplex> {
        return marimoDao.getEarnedAchievementsWithDetail().map { it.map() }
    }

    fun getLockedAchievements(): List<AchievementComplex> {
        return marimoDao.getLockedAchievementsWithDetail().map { it.map() }
    }

    fun resetAllAchievements() {
        marimoDao.resetAllAchievements()
    }

    fun addAchievementToTable(context: Context, resource: Int, resourceDetail: Int) {
        val achievementList = mutableListOf<Achievement>()
        val detailList = mutableListOf<AchievementDetail>()

        val packageName = context.packageName

        // Parse Achievements
        context.resources.openRawResource(resource).use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { riga ->
                    val colonne = riga.split(",")
                    if (colonne.size >= 9) {
                        val rawTitle = colonne[2].cleanValue().removePrefix("R.string.")
                        val rawDesc = colonne[3].cleanValue().removePrefix("R.string.")
                        val rawImg = colonne[5].cleanValue()
                        val imgResName = rawImg.removePrefix("R.string.").removePrefix("R.drawable.")
                        val imgResType = if (rawImg.startsWith("R.drawable.")) "drawable" else "string"

                        val titleResId = context.resources.getIdentifier(rawTitle,
                            "string", packageName)
                        val descResId = context.resources.getIdentifier(rawDesc,
                            "string", packageName)
                        val imgResId = context.resources.getIdentifier(imgResName,
                            imgResType, packageName)

                        val newAchievement = Achievement(
                            id = colonne[0].cleanValue().toIntOrNull() ?: 0,
                            code = colonne[1].cleanValue(),
                            titleID = titleResId,
                            descriptionValue = descResId,
                            imageId = imgResId,
                            backgroundColor = colonne[6].cleanValue(),
                            category = colonne[4].cleanValue(),
                            sortOrder = colonne[9].cleanValue().toIntOrNull() ?: 0,
                            earned = colonne[8].cleanValue().equals("True", ignoreCase = true),
                            date = colonne[8].cleanValue().takeIf { it.isNotEmpty() && it != "NULL" }
                        )
                        achievementList.add(newAchievement)
                    }
                }
            }
        }

        // Parse Achievement Details
        context.resources.openRawResource(resourceDetail).use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { riga ->
                    val colonne = riga.split(",")
                    if (colonne.size >= 8) {
                        val rawTypeDesc = colonne[3].cleanValue().removePrefix("R.string.")
                        val rawUnitDesc = colonne[5].cleanValue().removePrefix("R.string.")

                        val typeDescResId = context.resources.getIdentifier(rawTypeDesc,
                            "string", packageName)
                        val unitDescResId = context.resources.getIdentifier(rawUnitDesc,
                            "string", packageName)

                        val newDetail = AchievementDetail(
                            id = colonne[0].cleanValue().toIntOrNull() ?: 0,
                            achievement = colonne[0].cleanValue().toIntOrNull() ?: 0,
                            description = colonne[1].cleanValue(),
                            type = AchievementType.fromId(colonne[2].cleanValue().toIntOrNull() ?: 0),
                            typeDescription = typeDescResId,
                            unit = UnitType.fromId(colonne[4].cleanValue().toIntOrNull() ?: 0),
                            unitDescription = unitDescResId,
                            current = colonne[6].cleanValue().toIntOrNull() ?: 0,
                            target = colonne[7].cleanValue().toIntOrNull() ?: 0
                        )
                        detailList.add(newDetail)
                    }
                }
            }
        }

        if (achievementList.isNotEmpty()) {
            marimoDao.insertAchievements(achievementList.map { it.toDBModel() })
        }
        if (detailList.isNotEmpty()) {
            marimoDao.insertAchievementDetails(detailList.map { it.toDBModel() })
        }
    }

    fun earnAchievement(id:Int, date: String) {
        marimoDao.earnAchievement(id, date)
    }

    fun getAllAchievement() : List<AchievementComplex> {
        return marimoDao.getAllAchievement().map(){it.map()}
    }

    fun updateAchievementDetail(id: Int, current: Int): Boolean {
        marimoDao.updateAchievementDetail(id,current)
        val detail = marimoDao.getAchievementDetail(id).toAppModel<AchievementDetail>()
        return detail.current == detail.target
    }
}
