package com.example.radarsync.data

// TODO: Add the Retrofit library
import retrofit2.Response
import retrofit2.http.GET

interface PositionService {
    // TODO: Ensure that the path is correct
    @GET("/app/pos/")
    suspend fun getPositionData(): Response<List<PositionEntity>>
}
