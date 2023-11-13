package com.example.radarsync

import android.app.Application
import com.example.radarsync.data.PositionRepository

object DependencyProvider {
    private lateinit var positionRepository: PositionRepository
    fun initialize(application: Application) {
        positionRepository = PositionRepository(application)
    }

    fun getPositionRepository(): PositionRepository {
        return positionRepository
    }
}
