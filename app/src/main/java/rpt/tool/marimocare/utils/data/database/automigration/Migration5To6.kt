package rpt.tool.marimocare.utils.data.database.automigration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration5To6 : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Recreate achievement table to fix the default value of the 'earned' column
        // which changed from 'false' (string) to '0' (integer)
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='achievement'")
        val exists = cursor.moveToFirst()
        cursor.close()

        if (exists) {
            db.execSQL("DROP TABLE IF EXISTS `achievement_new`")
            db.execSQL("CREATE TABLE `achievement_new` (`id` INTEGER NOT NULL, `code` TEXT NOT NULL, `title` INTEGER NOT NULL, `description` INTEGER NOT NULL, `image` INTEGER NOT NULL, `color` TEXT NOT NULL, `category` TEXT NOT NULL, `order` INTEGER NOT NULL, `earned` INTEGER NOT NULL DEFAULT 0, `acquired_date` TEXT, PRIMARY KEY(`id`))")
            db.execSQL("INSERT INTO `achievement_new` (`id`, `code`, `title`, `description`, `image`, `color`, `category`, `order`, `earned`, `acquired_date`) SELECT `id`, `code`, `title`, `description`, `image`, `color`, `category`, `order`, `earned`, `acquired_date` FROM `achievement`")
            db.execSQL("DROP TABLE `achievement`")
            db.execSQL("ALTER TABLE `achievement_new` RENAME TO `achievement`")
        } else {
            db.execSQL("CREATE TABLE IF NOT EXISTS `achievement` (`id` INTEGER NOT NULL, `code` TEXT NOT NULL, `title` INTEGER NOT NULL, `description` INTEGER NOT NULL, `image` INTEGER NOT NULL, `color` TEXT NOT NULL, `category` TEXT NOT NULL, `order` INTEGER NOT NULL, `earned` INTEGER NOT NULL DEFAULT 0, `acquired_date` TEXT, PRIMARY KEY(`id`))")
        }

        // Ensure achievement_details table exists
        db.execSQL("CREATE TABLE IF NOT EXISTS `achievement_details` (`id` INTEGER NOT NULL, `achievement_id` INTEGER NOT NULL, `desc` TEXT NOT NULL, `type` INTEGER NOT NULL, `type_desc` INTEGER NOT NULL, `unit` INTEGER NOT NULL, `unit_desc` INTEGER NOT NULL, `current` INTEGER NOT NULL, `target` INTEGER NOT NULL, PRIMARY KEY(`id`))")

        // Create pot_decoration table (new in version 6)
        db.execSQL("CREATE TABLE IF NOT EXISTS `pot_decoration` (`id` TEXT NOT NULL, `marimo_code` INTEGER NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `colour` TEXT NOT NULL, `dimensions` TEXT NOT NULL, `material` TEXT NOT NULL, `notes` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`marimo_code`) REFERENCES `marimo`(`code`) ON UPDATE NO ACTION ON DELETE CASCADE )")

        // Create index for pot_decoration
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pot_decoration_marimo_code` ON `pot_decoration` (`marimo_code`)")
    }
}
