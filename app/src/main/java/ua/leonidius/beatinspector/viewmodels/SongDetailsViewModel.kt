package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.repos.SongsRepository

class SongDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val songsRepository: SongsRepository
): ViewModel() {

    enum class SongDetailsStatus {
        Loading,
        Loaded,
        Error
    }

    data class SongDetailsUiState(
        val status: SongDetailsStatus = SongDetailsStatus.Loading,
        val error: String? = null,
        val title: String = "",
        val artist: String = "",
        val bpm: String = "",
        val key: String = "",
        val genres: String = "",
    )

    var songDetails by mutableStateOf(SongDetailsUiState())
        private set

    init {
        val songId = savedStateHandle.get<String>("songId")!!
        loadSongDetails(songId)
    }

    private fun loadSongDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val _songDetails = try {
                val song = songsRepository.getTrackDetails(id)
                SongDetailsUiState(
                    status = SongDetailsStatus.Loaded,
                    title = song.name,
                    artist = song.artist,
                    bpm = song.bpm.toString(),
                    key = song.key,
                    genres = song.genres.joinToString(", ")
                )
            } catch (e: Exception) {
                SongDetailsUiState(
                    status = SongDetailsStatus.Error,
                    error = e.message
                )
            }

            withContext(Dispatchers.Main) {
                songDetails = _songDetails
            }
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return SongDetailsViewModel(extras.createSavedStateHandle(), app.songsRepository) as T
            }

        }

    }

}