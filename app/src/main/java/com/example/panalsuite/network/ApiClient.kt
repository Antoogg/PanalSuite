package com.example.panalsuite.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class ApiTestPost(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

interface PanalApiService {
    @GET("posts")
    suspend fun getPosts(): List<ApiTestPost>
}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: PanalApiService =
        retrofit.create(PanalApiService::class.java)
}
