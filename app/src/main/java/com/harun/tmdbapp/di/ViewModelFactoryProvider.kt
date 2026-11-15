package com.harun.tmdbapp.di

import com.harun.tmdbapp.ui.screen.detail.DetailViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ViewModelFactoryProvider {
    fun detailViewModelFactory(): DetailViewModel.Factory
}
