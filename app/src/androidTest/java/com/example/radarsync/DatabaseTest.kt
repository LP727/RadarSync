package com.example.radarsync

import android.content.Context
import android.util.Half.toFloat
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.radarsync.data.PositionDao
import com.example.radarsync.data.PositionDatabase
import com.example.radarsync.data.PositionEntity
import com.example.radarsync.utilities.FileHelper
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var positionDao: PositionDao
    private lateinit var database: PositionDatabase
    private lateinit var testContext: Context

    @Before
    fun createDb() {
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(testContext, PositionDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        positionDao = database.positionDao()!!
    }

    @Test
    fun createPositions() {
        // Load positions from test positions file
        val text = FileHelper.getTextFromAssets(testContext, "test_positions.json")
        val positions = FileHelper.parseText(text)
        positionDao.insertAll(positions)
        val count = positionDao.getCount()
        assertEquals(count, positions.size)
    }

    @Test
    fun insertPosition() {
        val testPosition = PositionEntity("randomStringHere", 0.0, 0.0, 1.0, toFloat(5), "DBTEST", 1000000000)
        positionDao.insertPosition(testPosition)
        val dbPosition = positionDao.getPositionById("randomStringHere")
        assertEquals(testPosition.latitude, dbPosition?.latitude ?: 0.0, 0.0)
        assertEquals(testPosition.longitude, dbPosition?.longitude ?: 0.0, 0.0)
        assertEquals(testPosition.altitude, dbPosition?.altitude ?: 1.0, 0.0)
        assertEquals(testPosition.accuracy, dbPosition?.accuracy ?: toFloat(0), toFloat(0))
        assertEquals(testPosition.name, dbPosition?.name ?: "")
        assertEquals(testPosition.time, dbPosition?.time ?: 0)
    }
    //TODO: Add test for deleting positions
    @After
    fun closeDb() {
        database.close()
    }
}
