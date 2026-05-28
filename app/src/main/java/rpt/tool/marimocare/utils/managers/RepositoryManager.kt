package rpt.tool.marimocare.utils.managers

import android.content.Context
import rpt.tool.marimocare.utils.data.repositories.MarimoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.MarimoCareApplication
import rpt.tool.marimocare.utils.data.database.AppDatabase
import rpt.tool.marimocare.utils.data.repositories.decoration.PotDecorationRepository

object RepositoryManager {

    private val ctx: Context
        get() = MarimoCareApplication.instance

    private val db by lazy { AppDatabase(ctx) }

    val marimoRepository: MarimoRepository by lazy {
        MarimoRepository(db.marimoDao())
    }

    val potDecorationRepository: PotDecorationRepository by lazy {
        PotDecorationRepository(db.potDecorationDao())
    }

    suspend fun clear() {
        withContext(Dispatchers.Default) {
            marimoRepository.clearAll()
        }
    }
}