package com.mjob.feednewsstore4.data.remote

import com.mjob.feednewsstore4.data.remote.model.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsRemoteDataSource {
    @GET("v1/latest-news?country=FR&language=en")
    suspend fun getLatestNews(): FeedResponse
}