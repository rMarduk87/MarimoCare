package rpt.tool.marimocare.utils.data.repositories.decoration

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import rpt.tool.marimocare.utils.data.appmodels.PotDecoration
import rpt.tool.marimocare.utils.data.database.dao.decoration.PotDecorationDao

class PotDecorationRepository(private val potDecorationDao: PotDecorationDao) {

    val allPotDecorations: LiveData<List<PotDecoration>> =
        potDecorationDao.getDecorations().map { it -> it.map { it.map() } }

    suspend fun getDecorationsForMarimo(marimoCode: Int): List<PotDecoration> {
        return potDecorationDao.getDecorationsForMarimo(marimoCode).map {
            it.toAppModel()
        }
    }

    suspend fun saveDecorations(marimoCode: Int, decorations: List<PotDecoration>) {
        potDecorationDao.deleteDecorationsForMarimo(marimoCode)
        if (decorations.isNotEmpty()) {
            potDecorationDao.insertAll(decorations.map {
                it.marimoCode = marimoCode
                it.toDBModel()
            })
        }
    }

    suspend fun deleteDecorationsForMarimo(marimoCode: Int) {
        potDecorationDao.deleteDecorationsForMarimo(marimoCode)
    }

    suspend fun getAllDecorations(): List<PotDecoration> {
        return potDecorationDao.getAllDecorations().map {
            it.toAppModel()
        }
    }
}
