package com.example.radarsync.data

// TODO: Add the Retrofit library
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PositionService {
    // TODO: Ensure that the path is correct
    @GET
    suspend fun getPositionData(@Url url: String): Response<List<PositionEntity>>
}
