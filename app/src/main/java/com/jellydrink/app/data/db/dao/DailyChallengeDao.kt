package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jellydrink.app.data.db.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {
    @Query("SELECT * FROM daily_challenges WHERE date = :date")
    fun getChallengeForDate(date: String): Flow<DailyChallengeEntity?>

    @Query("SELECT * FROM daily_challenges WHERE date = :date")
    suspend fun getChallengeForDateSync(date: String): DailyChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(challenge: DailyChallengeEntity)

    @Query("UPDATE daily_challenges SET currentProgress = :progress, completed = :completed WHERE date = :date")
    suspend fun updateProgress(date: String, progress: Int, completed: Boolean)

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE completed = 1")
    suspend fun getCompletedChallengesCount(): Int

    @Query("DELETE FROM daily_challenges")
    suspend fun deleteAll()
}
