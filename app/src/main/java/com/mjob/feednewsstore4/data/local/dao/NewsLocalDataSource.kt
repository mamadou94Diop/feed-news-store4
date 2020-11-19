package com.mjob.feednewsstore4.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.mjob.feednewsstore4.data.local.model.LocalNews
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsLocalDataSource {
    @Insert
    suspend fun insert(news: List<LocalNews>)

    @Query("SELECT * FROM news")
    fun read(): Flow<List<LocalNews>?>

    @Query("DELETE  FROM news")
    suspend fun deleteAll()

    @Transaction
    suspend fun update(news: List<LocalNews>) {
        deleteAll()
        insert(news)
    }
}