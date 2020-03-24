package com.kpstv.yts.viewmodels.providers

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kpstv.yts.data.db.repository.FavouriteRepository
import com.kpstv.yts.data.db.repository.MainRepository
import com.kpstv.yts.data.db.repository.PauseRepository
import com.kpstv.yts.interfaces.api.YTSPlaceholderApi
import com.kpstv.yts.viewmodels.MainViewModel

class MainViewModelFactory(
    private val repository: MainRepository,
    private val application: Application,
    private val ytsPlaceholderApi: YTSPlaceholderApi,
    private val favouriteRepository: FavouriteRepository,
    private val pauseRepository: PauseRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(
            application,
            ytsPlaceholderApi,
            repository,
            favouriteRepository,
            pauseRepository
        ) as T
    }
}