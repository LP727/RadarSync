package com.example.radarsync.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PositionEntity::class], version = 1, exportSchema = false)
abstract class PositionDatabase: RoomDatabase() {
    abstract fun positionDao(): PositionDao

    companion object {
        // Volatile, meaning that writes to this field are immediately made visible to other threads
        @Volatile
        private var INSTANCE: PositionDatabase? = null

        fun getInstance(context: Context): PositionDatabase {
            if (INSTANCE == null) {
                synchronized(PositionDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PositionDatabase::class.java,
                        "radarsync.db"
                    ).build()
                }
            }
            return INSTANCE !! // Assertion if INSTANCE is null
        }
    }
}
