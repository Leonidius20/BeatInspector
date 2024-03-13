package ua.leonidius.beatinspector.features.tracklist.playlist.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.playlists.PlaylistInfoRepository
import ua.leonidius.beatinspector.features.tracklist.shared.viewmodels.TrackListViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PlaylistContentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistInfoRepository: PlaylistInfoRepository,
    @Named("playlist_content") pagingSource: PagingDataSource<SongSearchResult>
): TrackListViewModel(pagingSource) {

    private val playlistId = savedStateHandle.get<String>("playlistId")!!

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

    /*companion object {

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
    }*/

}