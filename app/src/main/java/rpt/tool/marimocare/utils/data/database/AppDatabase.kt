package rpt.tool.marimocare.utils.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import rpt.tool.marimocare.utils.data.database.DatabaseHelper.Companion.databaseName
import rpt.tool.marimocare.utils.data.database.automigration.Migration5To6
import rpt.tool.marimocare.utils.data.database.dao.*
import rpt.tool.marimocare.utils.data.database.dao.decoration.PotDecorationDao
import rpt.tool.marimocare.utils.data.database.models.*
import rpt.tool.marimocare.utils.data.database.models.decoration.PotDecorationModel

@Database(
    entities = [
        MarimoModel::class,
        MarimoChangeModel::class,
        MarimoQRModel::class,
        MarimoHealthScoreModel::class,
        PotDecorationModel::class,
        AchievementModel::class,
        AchievementDetailModel::class,
        ChatHistoryModel::class,
    ],
    version = 7,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 6, to = 7),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun marimoDao(): MarimoDao
    abstract fun potDecorationDao(): PotDecorationDao
    abstract fun chatHistoryDao(): ChatHistoryDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var instance: AppDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName,
        )
            // Usa il nuovo oggetto isolato Migration5To6
            .addMigrations(Migration5To6)
            .build()
    }
}