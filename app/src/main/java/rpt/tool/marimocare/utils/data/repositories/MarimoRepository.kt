package rpt.tool.marimocare.utils.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScore
import rpt.tool.marimocare.utils.data.appmodels.MarimoHealthScoreStats
import rpt.tool.marimocare.utils.data.appmodels.MarimoQR
import rpt.tool.marimocare.utils.data.database.dao.MarimoDao
import kotlin.collections.map

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getSpecificHealth(marimoCode: Int, last: String?): Int {
        return marimoDao.getCurrentHealth(marimoCode, last ?: AppUtils.getCurrentDate())
    }


    val marimos: LiveData<List<Marimo>> =
        marimoDao.getMarimos().map { it.map { it.map() } }

}