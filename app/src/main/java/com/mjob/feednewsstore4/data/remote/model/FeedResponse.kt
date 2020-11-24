package com.mjob.feednewsstore4.data.remote.model

import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @SerializedName("news")
    val news: List<NewsResponse>
)