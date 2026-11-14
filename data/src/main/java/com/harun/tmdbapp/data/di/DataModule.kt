package com.harun.tmdbapp.data.di

import android.content.Context
import androidx.room.Room
import com.harun.tmdbapp.data.local.dao.BookmarkDao
import com.harun.tmdbapp.data.local.database.TmdbDatabase
import com.harun.tmdbapp.data.remote.retrofit.network.ApiService
import com.harun.tmdbapp.data.remote.retrofit.network.Network
import com.harun.tmdbapp.data.repository.BookmarkRepositoryImpl
import com.harun.tmdbapp.data.repository.MovieRepository
import com.harun.tmdbapp.repository.BookmarkRepository
import com.harun.tmdbapp.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepository: MovieRepository
    ): Repository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        bookmarkRepository: BookmarkRepositoryImpl
    ): BookmarkRepository

    companion object {
        @Provides
        @Singleton
        fun provideApiService(): ApiService = Network.api

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): TmdbDatabase {
            return Room.databaseBuilder(
                context,
                TmdbDatabase::class.java,
                "tmdb_database.db"
            ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
        }

        @Provides
        fun provideBookmarkDao(database: TmdbDatabase): BookmarkDao {
            return database.bookmarkDao()
        }
    }
}
