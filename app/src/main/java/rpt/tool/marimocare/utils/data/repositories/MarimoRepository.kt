package rpt.tool.marimocare.utils.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.database.dao.MarimoDao

class MarimoRepository(
    private val marimoDao: MarimoDao
) {
    fun clearAll() {
        marimoDao.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addMarimo(marimoName: String, lastWaterChange: String, notes: String, freq: Int) {
        Marimo(marimoDao.getLastId()+1,marimoName, freq,
            lastWaterChange, AppUtils.nextChange(
            lastWaterChange,
            freq), notes, AppUtils.daysUntil(
            AppUtils.nextChange(
                lastWaterChange,
                freq))).let {

            marimoDao.insert(it.map())
        }
    }

    val marimos: LiveData<List<Marimo>> =
        marimoDao.getMarimos().map { it.map { it.map() } }

}