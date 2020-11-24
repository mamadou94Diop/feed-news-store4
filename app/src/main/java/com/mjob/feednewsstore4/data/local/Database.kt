package com.mjob.feednewsstore4.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mjob.feednewsstore4.data.local.dao.NewsLocalDataSource
import com.mjob.feednewsstore4.data.local.model.LocalNews

@Database(entities = [LocalNews::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun dao(): NewsLocalDataSource
}