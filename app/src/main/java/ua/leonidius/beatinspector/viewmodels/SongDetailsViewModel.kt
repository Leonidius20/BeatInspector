package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.domain.entities.SongDetails
import ua.leonidius.beatinspector.domain.usecases.LoadSongDetailsUseCase

class SongDetailsViewModel(
    private val loadSongUseCase: LoadSongDetailsUseCase
): ViewModel() {

    var songDetails by mutableStateOf(SongDetails.Dummy)
        private set



    // todo have a state class with all fields needed for
    // display, but in display-ready format (e.g. bpm as string)

    // todo: we can move initial loading to init {}, but then we need to
    //  pass songId through a savedStateHandle through the factory
    fun loadSongDetails(id: String) {
        viewModelScope.launch {
            songDetails = loadSongUseCase.loadSongDetails(id)
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return SongDetailsViewModel(app.songDetailsUseCase) as T
            }

        }

    }

}