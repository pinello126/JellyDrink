package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jellydrink.app.data.db.entity.DailyGoalEntity

@Dao
interface DailyGoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(goal: DailyGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: DailyGoalEntity)

    @Query("SELECT * FROM daily_goal WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getGoalsForRange(startDate: String, endDate: String): List<DailyGoalEntity>

    @Query("INSERT OR IGNORE INTO daily_goal (date, goalMl) SELECT DISTINCT date, :goalMl FROM water_intake")
    suspend fun seedMissingDates(goalMl: Int)

    @Query("DELETE FROM daily_goal")
    suspend fun deleteAll()
}
