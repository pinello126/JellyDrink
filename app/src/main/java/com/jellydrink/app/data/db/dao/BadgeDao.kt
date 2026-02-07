package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jellydrink.app.data.db.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: BadgeEntity)

    @Query("SELECT * FROM badges ORDER BY dateEarned DESC")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM badges WHERE type = :type)")
    suspend fun hasBadge(type: String): Boolean

    @Query("DELETE FROM badges")
    suspend fun deleteAll()
}
