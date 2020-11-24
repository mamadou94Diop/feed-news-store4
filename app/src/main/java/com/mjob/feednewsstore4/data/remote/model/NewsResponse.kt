package com.mjob.feednewsstore4.data.remote.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class NewsResponse(
    @SerializedName("author")
    val author: String,
    @SerializedName("category")
    val category: List<String>,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("published")
    val publicationDate: Date,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)