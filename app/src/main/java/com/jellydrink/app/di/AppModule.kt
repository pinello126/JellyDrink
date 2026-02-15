package com.jellydrink.app.di

import android.content.Context
import androidx.room.Room
import com.jellydrink.app.data.db.AppDatabase
import com.jellydrink.app.data.db.dao.BadgeDao
import com.jellydrink.app.data.db.dao.DailyChallengeDao
import com.jellydrink.app.data.db.dao.DailyGoalDao
import com.jellydrink.app.data.db.dao.DecorationDao
import com.jellydrink.app.data.db.dao.JellyfishDao
import com.jellydrink.app.data.db.dao.UserProfileDao
import com.jellydrink.app.data.db.dao.WaterIntakeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "jellydrink_db"
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6
            )
            .build()
    }

    @Provides
    fun provideWaterIntakeDao(database: AppDatabase): WaterIntakeDao {
        return database.waterIntakeDao()
    }

    @Provides
    fun provideBadgeDao(database: AppDatabase): BadgeDao {
        return database.badgeDao()
    }

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideDailyChallengeDao(database: AppDatabase): DailyChallengeDao {
        return database.dailyChallengeDao()
    }

    @Provides
    fun provideJellyfishDao(database: AppDatabase): JellyfishDao {
        return database.jellyfishDao()
    }

    @Provides
    fun provideDecorationDao(database: AppDatabase): DecorationDao {
        return database.decorationDao()
    }

    @Provides
    fun provideDailyGoalDao(database: AppDatabase): DailyGoalDao {
        return database.dailyGoalDao()
    }
}
