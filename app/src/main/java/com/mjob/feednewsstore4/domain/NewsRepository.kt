package com.mjob.feednewsstore4.domain

import com.mjob.feednewsstore4.domain.model.News
import com.mjob.feednewsstore4.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getLatestNews(): Flow<Result<List<News>>>
}