package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jellydrink.app.data.db.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET xp = xp + :amount WHERE id = 1")
    suspend fun addXp(amount: Int)

    @Query("UPDATE user_profile SET level = :level WHERE id = 1")
    suspend fun updateLevel(level: Int)

    @Query("UPDATE user_profile SET totalMlAllTime = totalMlAllTime + :amount WHERE id = 1")
    suspend fun addTotalMl(amount: Int)

    @Query("UPDATE user_profile SET dailyRecord = :record WHERE id = 1")
    suspend fun updateDailyRecord(record: Int)

    @Query("UPDATE user_profile SET bestStreak = :streak WHERE id = 1")
    suspend fun updateBestStreak(streak: Int)

    @Query("UPDATE user_profile SET activeDays = activeDays + 1 WHERE id = 1")
    suspend fun incrementActiveDays()

    @Query("DELETE FROM user_profile")
    suspend fun deleteAll()
}
