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
        positionDao = database.positionDao()
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
        assertEquals(testPosition, dbPosition)
    }
    //TODO: Add test for deleting positions

    @Test
    fun deletePositions()
    {
        val testPosition1 = PositionEntity("randomString1Here", 45.0, 92.0, 1.0, toFloat(5), "DBTEST1", 1000000000)
        val testPosition2 = PositionEntity("randomString2Here", 65.0, 93.0, 100.0, toFloat(5), "DBTEST2", 2000000000)
        val testPosition3 = PositionEntity("randomString3Here", 25.0, 94.0, 2000.0, toFloat(5), "DBTEST3", 3000000000)
        positionDao.insertPosition(testPosition1)
        positionDao.insertPosition(testPosition2)
        positionDao.insertPosition(testPosition3)

        var posCount = positionDao.getCount()
        assertEquals(posCount, 3)

        val dbPosList = positionDao.getAll()
        assertEquals(dbPosList[0], testPosition1)
        assertEquals(dbPosList[1], testPosition2)
        assertEquals(dbPosList[2], testPosition3)

        positionDao.deletePosition("randomString1Here")
        posCount = positionDao.getCount()
        assertEquals(posCount, 2)

        val missingPosition = positionDao.getPositionById("randomString1Here")
        assertEquals(missingPosition, null)

        positionDao.deleteAllPositions()
        posCount = positionDao.getCount()
        assertEquals(posCount, 0)

        val missingPosition2 = positionDao.getPositionById("randomString2Here")
        assertEquals(missingPosition2, null)
        val missingPosition3 = positionDao.getPositionById("randomString3Here")
        assertEquals(missingPosition3, null)
    }
    @After
    fun closeDb() {
        database.close()
    }
}
