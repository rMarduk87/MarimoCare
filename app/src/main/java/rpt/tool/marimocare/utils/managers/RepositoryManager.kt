package rpt.tool.marimocare.utils.managers

import android.content.Context
import rpt.tool.marimocare.utils.data.repositories.MarimoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.MarimoCareApplication
import rpt.tool.marimocare.utils.data.database.AppDatabase

object RepositoryManager {

    private val ctx: Context
        get() = MarimoCareApplication.instance

    val marimoRepository: MarimoRepository by lazy {
        val db = AppDatabase(ctx)
        MarimoRepository(
            db.marimoDao()
        )
    }

    suspend fun clear() {
        withContext(Dispatchers.Default) {
            marimoRepository.clearAll()
        }
    }
}
