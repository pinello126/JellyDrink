package com.jellydrink.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jellydrink.app.data.db.entity.DecorationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DecorationDao {
    @Query("SELECT * FROM decorations")
    fun getAllDecorations(): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM decorations WHERE placed = 1")
    fun getPlacedDecorations(): Flow<List<DecorationEntity>>

    @Query("SELECT * FROM decorations WHERE id = :id")
    suspend fun getDecorationById(id: String): DecorationEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(decoration: DecorationEntity)

    @Query("UPDATE decorations SET owned = 1, placed = 1 WHERE id = :id")
    suspend fun purchase(id: String)

    @Query("UPDATE decorations SET placed = :placed WHERE id = :id")
    suspend fun setPlaced(id: String, placed: Boolean)

    @Query("SELECT COUNT(*) FROM decorations")
    suspend fun getCount(): Int

    @Query("DELETE FROM decorations")
    suspend fun deleteAll()
}
