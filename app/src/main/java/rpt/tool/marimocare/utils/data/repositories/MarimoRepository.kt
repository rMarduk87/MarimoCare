package rpt.tool.marimocare.utils.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.checkerframework.checker.units.qual.s
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.appmodels.MarimoChange
import rpt.tool.marimocare.utils.data.database.dao.MarimoDao
import kotlin.collections.map

class MarimoRepository(
    private val marimoDao: MarimoDao
) {
    fun clearAll() {
        marimoDao.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addMarimo(marimoName: String, lastWaterChange: String, notes: String, freq: Int) : Int {
        Marimo(marimoDao.getLastId()+1,marimoName, freq,
            lastWaterChange, AppUtils.nextChange(
            lastWaterChange,
            freq), notes, AppUtils.daysUntil(
            AppUtils.nextChange(
                lastWaterChange,
                freq))).let {

            marimoDao.insert(it.map())
        }

        return marimoDao.getLastId()
    }

    fun getMarimo(marimoCode: Int): Marimo? {
        return marimoDao.getMarimo(marimoCode).map()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMarimo(code: Int, name: String, lastWater: String, notes: String, freq: Int) {
        marimoDao.update(code,name, freq,
                lastWater, notes)
    }

    fun updateWaterMarimo(lastChanged: String, code: Int) {
        marimoDao.updateWater(lastChanged, code)
    }

    fun getAllSync():List<Marimo> {
        return marimoDao.getAll().map { it.map() }
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

    fun addWaterChanges(id: Int, lastWater: String) {
        MarimoChange(marimoDao.getLastIdFromWaterChanges()+1,id.toString(),
            lastWater).let {

            marimoDao.insertWaterChanges(it.map())
        }
    }

    fun getTotalWaterChanged() : Int {
        return marimoDao.getTotalWaterChanges()
    }

    fun getAllChanges(): List<MarimoChange> {
        return marimoDao.getAllWaterChanges().map { it.map() }
    }

    fun deleteMarimo(code: Int) {
        marimoDao.delete(code)
    }

    val marimos: LiveData<List<Marimo>> =
        marimoDao.getMarimos().map { it.map { it.map() } }



}