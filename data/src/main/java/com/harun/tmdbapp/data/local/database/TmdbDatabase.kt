package com.harun.tmdbapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harun.tmdbapp.data.local.dao.BookmarkDao
import com.harun.tmdbapp.data.local.entity.BookmarkEntity

@Database(entities = [BookmarkEntity::class], version = 1, exportSchema = false)
abstract class TmdbDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
