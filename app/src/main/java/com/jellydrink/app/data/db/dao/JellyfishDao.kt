package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jellydrink.app.data.db.entity.JellyfishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JellyfishDao {
    @Query("SELECT * FROM jellyfish_collection")
    fun getAllJellyfish(): Flow<List<JellyfishEntity>>

    @Query("SELECT * FROM jellyfish_collection WHERE selected = 1 LIMIT 1")
    fun getSelectedJellyfish(): Flow<JellyfishEntity?>

    @Query("SELECT * FROM jellyfish_collection WHERE selected = 1 LIMIT 1")
    suspend fun getSelectedJellyfishSync(): JellyfishEntity?

    @Query("SELECT * FROM jellyfish_collection WHERE id = :id")
    suspend fun getJellyfishById(id: String): JellyfishEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(jellyfish: JellyfishEntity)

    @Query("UPDATE jellyfish_collection SET selected = 0")
    suspend fun deselectAll()

    @Query("UPDATE jellyfish_collection SET selected = 1 WHERE id = :id")
    suspend fun select(id: String)

    @Query("UPDATE jellyfish_collection SET unlocked = 1, dateUnlocked = :date WHERE id = :id")
    suspend fun unlock(id: String, date: String)

    @Query("SELECT COUNT(*) FROM jellyfish_collection")
    suspend fun getCount(): Int

    @Query("DELETE FROM jellyfish_collection")
    suspend fun deleteAll()
}
