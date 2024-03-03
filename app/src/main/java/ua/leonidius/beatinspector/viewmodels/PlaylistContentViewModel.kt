package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.PagingDataSource
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.playlists.PlaylistInfoRepository

class PlaylistContentViewModel(
    private val playlistInfoRepository: PlaylistInfoRepository,
    private val playlistId: String,
    pagingSource: PagingDataSource<SongSearchResult>
): TrackListViewModel(pagingSource) {

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set

    init {
        viewModelScope.launch {
            val result = playlistInfoRepository.get(playlistId)
            uiState = UiState.Loaded(
                playlistName = result.name,
                playlistCoverUrl = result.bigImageUrl,
                uri = result.uri,
            )
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Loaded(
            val playlistName: String,
            val playlistCoverUrl: String?,
            val uri: String,
        ) : UiState()
    }

    companion object {

        val Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                val savedStateHandle = extras.createSavedStateHandle()

                return PlaylistContentViewModel(
                    app.playlistInfoRepository,
                    savedStateHandle.get<String>("playlistId")!!,
                    app.playlistDataSourceFactory(savedStateHandle.get<String>("playlistId")!!),
                ) as T
            }

        }
    }

}