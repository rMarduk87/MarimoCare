package rpt.tool.marimocare.utils.data.database

import android.content.Context
import androidx.room.*
import rpt.tool.marimocare.utils.data.database.DatabaseHelper.Companion.databaseName
import rpt.tool.marimocare.utils.data.database.dao.*
import rpt.tool.marimocare.utils.data.database.models.*


@Database(
    entities = [
        MarimoModel::class,
        MarimoChangeModel::class,
        MarimoQRModel::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ]
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun marimoDao(): MarimoDao


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var instance: AppDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        )
            .build()
    }
}
