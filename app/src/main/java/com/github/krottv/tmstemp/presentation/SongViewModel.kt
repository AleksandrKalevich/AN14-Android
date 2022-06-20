package com.github.krottv.tmstemp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.krottv.tmstemp.data.SongsRepository
import com.github.krottv.tmstemp.domain.AlbumType
import com.github.krottv.tmstemp.domain.SongModel
import com.github.krottv.tmstemp.view.mymus.TracksMyMusicDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SongViewModel(private val songRepository: SongsRepository, private val tracksMyMusicDataSource: TracksMyMusicDataSource): ViewModel() {

    private val _state = MutableStateFlow<Result<List<SongModel>>?>(null)
    val state: StateFlow<Result<List<SongModel>>?> = _state

    private var downloadingJob: Job? = null

    fun loadData(albumType: AlbumType) {
        downloadingJob?.cancel()

        downloadingJob = viewModelScope.launch(Dispatchers.IO) {
            val result = try {
                Result.success(songRepository.getSongs(albumType))
            } catch (exception: Throwable) {
                Result.failure(exception)
            }
            _state.emit(result)
        }
    }

    fun loadMyData() {
        downloadingJob?.cancel()

        downloadingJob = viewModelScope.launch(Dispatchers.IO) {
            val result = try {
                Result.success(tracksMyMusicDataSource.getTracks())
            } catch (exception: Throwable) {
                Result.failure(exception)
            }
            _state.emit(result)
        }
    }
}