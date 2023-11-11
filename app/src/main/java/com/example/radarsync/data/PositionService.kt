package com.example.radarsync.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PositionService {
    @GET
    suspend fun getPositionData(@Url url: String): Response<List<PositionEntity>>
}
