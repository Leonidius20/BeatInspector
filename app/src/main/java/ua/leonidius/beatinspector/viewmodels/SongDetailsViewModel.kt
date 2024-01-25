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
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Song
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
        val errorMsgId: Int? = null,
        val title: String = "",
        val artists: String = "",
        val bpm: String = "",
        val key: String = "",
        val genres: String = "",
        val albumArtUrl: String = "",
    )

    var songDetails by mutableStateOf(SongDetailsUiState())
        private set

    init {
        val songId = savedStateHandle.get<String>("songId")!!
        loadSongDetails(songId)
    }

    private fun loadSongDetails(id: String) {
        viewModelScope.launch {
            val _songDetails = try {
                val song = songsRepository.getTrackDetails(id)
                SongDetailsUiState(
                    status = SongDetailsStatus.Loaded,
                    title = song.name,
                    artists = song.artist,
                    bpm = song.bpm.toString(),
                    key = song.key,
                    genres = song.genres.joinToString(", "),
                    albumArtUrl = song.albumArtUrl,
                )
            } catch (e: SongDataIOException) {
                SongDetailsUiState(
                    status = SongDetailsStatus.Error,
                    errorMsgId = e.toUiMessage()
                )
            } catch (e: Exception) {
                SongDetailsUiState(
                    status = SongDetailsStatus.Error,
                    errorMsgId = R.string.unknown_error
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