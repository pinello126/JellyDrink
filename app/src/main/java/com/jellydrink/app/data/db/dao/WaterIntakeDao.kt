package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jellydrink.app.data.db.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {

    @Insert
    suspend fun insert(intake: WaterIntakeEntity)

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_intake WHERE date = :date")
    fun getTotalForDate(date: String): Flow<Int>

    @Query("SELECT * FROM water_intake WHERE date = :date ORDER BY timestamp DESC")
    fun getIntakesForDate(date: String): Flow<List<WaterIntakeEntity>>

    @Query("""
        SELECT DISTINCT date FROM water_intake
        GROUP BY date
        HAVING SUM(amountMl) >= :goal
        ORDER BY date DESC
    """)
    suspend fun getDatesWithGoalMet(goal: Int): List<String>

    @Query("""
        SELECT date, SUM(amountMl) as totalMl
        FROM water_intake
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailySummary(startDate: String, endDate: String): List<DailySummary>

    @Query("SELECT COUNT(*) FROM water_intake")
    suspend fun getTotalEntries(): Int

    @Query("DELETE FROM water_intake")
    suspend fun deleteAll()
}

data class DailySummary(
    val date: String,
    val totalMl: Int
)
