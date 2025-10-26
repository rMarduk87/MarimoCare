package rpt.tool.marimocare.utils.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.data.database.dao.MarimoDao

class MarimoRepository(
    private val marimoDao: MarimoDao
) {
    fun clearAll() {
        marimoDao.clear()
    }

    val marimos: LiveData<List<Marimo>> =
        marimoDao.getMarimos().map { it.map { it.map() } }



}