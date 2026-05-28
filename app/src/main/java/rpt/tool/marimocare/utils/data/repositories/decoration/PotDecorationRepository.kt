package rpt.tool.marimocare.utils.data.repositories.decoration

import rpt.tool.marimocare.utils.data.appmodels.decoration.PotDecoration
import rpt.tool.marimocare.utils.data.database.dao.decoration.PotDecorationDao
import rpt.tool.marimocare.utils.data.database.mappers.decoration.PotDecorationMappers

class PotDecorationRepository(private val potDecorationDao: PotDecorationDao) {

    fun getDecorationsForMarimo(marimoCode: Int): List<PotDecoration> {
        return potDecorationDao.getDecorationsForMarimo(marimoCode).map {
            PotDecorationMappers.fromModel(it)
        }
    }

    fun saveDecorations(marimoCode: Int, decorations: List<PotDecoration>) {
        potDecorationDao.deleteDecorationsForMarimo(marimoCode)
        if (decorations.isNotEmpty()) {
            potDecorationDao.insertAll(decorations.map {
                PotDecorationMappers.toModel(it, marimoCode)
            })
        }
    }

    fun deleteDecorationsForMarimo(marimoCode: Int) {
        potDecorationDao.deleteDecorationsForMarimo(marimoCode)
    }
}