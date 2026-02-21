package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jellydrink.app.data.db.entity.BeerIntakeEntity
import kotlinx.coroutines.flow.Flow

data class BeerDailySummary(val date: String, val totalCl: Int)

@Dao
interface BeerIntakeDao {
    @Insert
    suspend fun insert(intake: BeerIntakeEntity)

    @Query("SELECT COALESCE(SUM(amountCl), 0) FROM beer_intake WHERE date = :date")
    fun getTotalForDate(date: String): Flow<Int>

    @Query("""
        SELECT date, SUM(amountCl) as totalCl
        FROM beer_intake
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailySummary(startDate: String, endDate: String): List<BeerDailySummary>

    @Query("DELETE FROM beer_intake")
    suspend fun deleteAll()
}
