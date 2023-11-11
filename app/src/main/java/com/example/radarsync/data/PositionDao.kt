package com.example.radarsync.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosition(position: PositionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(positions: List<PositionEntity>)

    @Query("SELECT * FROM pos ORDER BY time ASC")
    fun getAll(): List<PositionEntity>

    @Query("SELECT * FROM pos WHERE id = :id")
    fun getPositionById(id: String): PositionEntity?

    @Query("SELECT COUNT(*) FROM pos")
    fun getCount(): Int

    @Query("DELETE FROM pos")
    fun deleteAllPositions(): Int

    @Query("DELETE FROM pos WHERE id = :id")
    fun deletePosition(id: String)
}
