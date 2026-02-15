package com.jellydrink.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jellydrink.app.data.db.dao.BadgeDao
import com.jellydrink.app.data.db.dao.DailyChallengeDao
import com.jellydrink.app.data.db.dao.DecorationDao
import com.jellydrink.app.data.db.dao.JellyfishDao
import com.jellydrink.app.data.db.dao.UserProfileDao
import com.jellydrink.app.data.db.dao.DailyGoalDao
import com.jellydrink.app.data.db.dao.WaterIntakeDao
import com.jellydrink.app.data.db.entity.BadgeEntity
import com.jellydrink.app.data.db.entity.DailyGoalEntity
import com.jellydrink.app.data.db.entity.DailyChallengeEntity
import com.jellydrink.app.data.db.entity.DecorationEntity
import com.jellydrink.app.data.db.entity.JellyfishEntity
import com.jellydrink.app.data.db.entity.UserProfileEntity
import com.jellydrink.app.data.db.entity.WaterIntakeEntity

@Database(
    entities = [
        WaterIntakeEntity::class,
        BadgeEntity::class,
        UserProfileEntity::class,
        DailyChallengeEntity::class,
        JellyfishEntity::class,
        DecorationEntity::class,
        DailyGoalEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun jellyfishDao(): JellyfishDao
    abstract fun decorationDao(): DecorationDao
    abstract fun dailyGoalDao(): DailyGoalDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // UserProfile
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER PRIMARY KEY NOT NULL,
                        xp INTEGER NOT NULL DEFAULT 0,
                        level INTEGER NOT NULL DEFAULT 1,
                        totalMlAllTime INTEGER NOT NULL DEFAULT 0,
                        bestStreak INTEGER NOT NULL DEFAULT 0,
                        activeDays INTEGER NOT NULL DEFAULT 0,
                        dailyRecord INTEGER NOT NULL DEFAULT 0,
                        jellyfishStage INTEGER NOT NULL DEFAULT 0,
                        consecutiveActiveDays INTEGER NOT NULL DEFAULT 0,
                        lastActiveDate TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
                db.execSQL("INSERT OR IGNORE INTO user_profile (id) VALUES (1)")

                // DailyChallenge
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_challenges (
                        date TEXT PRIMARY KEY NOT NULL,
                        type TEXT NOT NULL,
                        targetValue INTEGER NOT NULL,
                        currentProgress INTEGER NOT NULL DEFAULT 0,
                        completed INTEGER NOT NULL DEFAULT 0,
                        xpReward INTEGER NOT NULL DEFAULT 30
                    )
                """.trimIndent())

                // JellyfishCollection
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS jellyfish_collection (
                        id TEXT PRIMARY KEY NOT NULL,
                        nameIt TEXT NOT NULL,
                        unlocked INTEGER NOT NULL DEFAULT 0,
                        selected INTEGER NOT NULL DEFAULT 0,
                        unlockCondition TEXT NOT NULL,
                        dateUnlocked TEXT
                    )
                """.trimIndent())

                // Decorations
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS decorations (
                        id TEXT PRIMARY KEY NOT NULL,
                        nameIt TEXT NOT NULL,
                        cost INTEGER NOT NULL,
                        owned INTEGER NOT NULL DEFAULT 0,
                        placed INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Remove jellyfishStage and consecutiveActiveDays from user_profile
                db.execSQL("""
                    CREATE TABLE user_profile_new (
                        id INTEGER PRIMARY KEY NOT NULL,
                        xp INTEGER NOT NULL DEFAULT 0,
                        level INTEGER NOT NULL DEFAULT 1,
                        totalMlAllTime INTEGER NOT NULL DEFAULT 0,
                        bestStreak INTEGER NOT NULL DEFAULT 0,
                        activeDays INTEGER NOT NULL DEFAULT 0,
                        dailyRecord INTEGER NOT NULL DEFAULT 0,
                        lastActiveDate TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())

                db.execSQL("""
                    INSERT INTO user_profile_new (id, xp, level, totalMlAllTime, bestStreak, activeDays, dailyRecord, lastActiveDate)
                    SELECT id, xp, level, totalMlAllTime, bestStreak, activeDays, dailyRecord, lastActiveDate
                    FROM user_profile
                """.trimIndent())

                db.execSQL("DROP TABLE user_profile")
                db.execSQL("ALTER TABLE user_profile_new RENAME TO user_profile")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add cost column to jellyfish_collection
                db.execSQL("ALTER TABLE jellyfish_collection ADD COLUMN cost INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add spendableXp column to user_profile and initialize with current xp value
                db.execSQL("ALTER TABLE user_profile ADD COLUMN spendableXp INTEGER NOT NULL DEFAULT 0")
                // Copy current xp to spendableXp for existing users
                db.execSQL("UPDATE user_profile SET spendableXp = xp")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_goal (
                        date TEXT PRIMARY KEY NOT NULL,
                        goalMl INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
    }
}
