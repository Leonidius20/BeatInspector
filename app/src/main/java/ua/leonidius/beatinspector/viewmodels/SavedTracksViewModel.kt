package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.saved_tracks.SavedTracksNetworkPagingSource
import ua.leonidius.beatinspector.repos.saved_tracks.SavedTracksRepository

class SavedTracksViewModel(
    private val savedTracksRepository: SavedTracksRepository,
    private val pagingSource: SavedTracksNetworkPagingSource, // todo: remove
): ViewModel() {

    val flow = Pager(PagingConfig(pageSize = 20)) {
        pagingSource
    }.flow.cachedIn(viewModelScope)

    sealed class UiState {

        object Loading: UiState()

        data class Error(
            val errorMsgId: Int,
            val errorAdditionalInfo: String,
        ): UiState()

        data class Loaded(
            val tracks: List<SongSearchResult>,
        ): UiState()

    }

    //var uiState by mutableStateOf<UiState>(UiState.Loading)
    //    private set

    init {
       // load()
    }

    private fun load() {
        /*uiState = UiState.Loading
        viewModelScope.launch {
            uiState = try {
                val result = savedTracksRepository.get()
                UiState.Loaded(result)
            } catch (error: SongDataIOException) {
                UiState.Error(
                    error.toUiMessage(),
                    error.toTextDescription()
                )
            }
        }*/
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return SavedTracksViewModel(
                    app.savedTracksRepository,
                    app.savedTracksNetworkPagingSource,
                ) as T
            }

        }

    }

}