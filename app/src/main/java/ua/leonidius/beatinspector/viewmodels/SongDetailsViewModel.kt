package ua.leonidius.beatinspector.viewmodels

import android.util.Log
import android.widget.Toast
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
import java.text.DecimalFormat

class SongDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val songsRepository: SongsRepository,
    private val decimalFormat: DecimalFormat,
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
        val timeSignatureOver4: Int = 0,
        val loudness: String = "",
        val genres: String = "",
        val albumArtUrl: String = "",
        val failedArtists: List<String> = emptyList()
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
                val result = songsRepository.getTrackDetails(id)
                val (song, failedArtists) = result
                SongDetailsUiState(
                    status = SongDetailsStatus.Loaded,
                    title = song.name,
                    artists = song.artist,
                    bpm = decimalFormat.format(song.bpm),
                    key = song.key,
                    timeSignatureOver4 = song.timeSignature,
                    loudness = decimalFormat.format(song.loudness) + " db",
                    genres = song.genres.joinToString(", "),
                    albumArtUrl = song.albumArtUrl,
                    failedArtists = failedArtists
                )
            } catch (e: SongDataIOException) {
                SongDetailsUiState(
                    status = SongDetailsStatus.Error,
                    errorMsgId = e.toUiMessage()
                )
            } catch (e: Exception) {
                Log.e("SongDetailsViewModel", "Unknown error", e)
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

                return SongDetailsViewModel(extras.createSavedStateHandle(), app.songsRepository, app.decimalFormat) as T
            }

        }

    }


}