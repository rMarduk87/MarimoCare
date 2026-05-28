package rpt.tool.marimocare.utils.data.database

import android.content.Context
import androidx.room.*
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
    ],
    version = 6,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6, spec = Migration5To6::class),
    ],
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun marimoDao(): MarimoDao
    abstract fun potDecorationDao(): PotDecorationDao

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
            databaseName,
        )
            .build()
    }
}
