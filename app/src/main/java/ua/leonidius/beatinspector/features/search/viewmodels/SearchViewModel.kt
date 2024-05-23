package ua.leonidius.beatinspector.features.search.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepository
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.features.shared.model.toUiMessage
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
) : ViewModel() {

    sealed class UiState {

        object Uninitialized : UiState()

        object Loading : UiState()

        data class Loaded(
            val searchResults: List<SongSearchResult>,
        ) : UiState()

        data class Error(
            val errorMessageId: Int,
            val errorAdditionalInfo: String,
        ) : UiState()

    }


    var query by mutableStateOf(savedStateHandle["query"] ?: "")

    var uiState by mutableStateOf<UiState>(UiState.Uninitialized)
        private set

    init {
        if (query.isNotEmpty()) {
            performSearch()
        }
    }

    fun performSearch() {
        //searchQueriesFlow.emit(query)

        viewModelScope.launch {
            try {
                uiState = UiState.Loading
                val results = searchRepository.get(query)
                uiState = UiState.Loaded(results)
            } catch (e: SongDataIOException) {
                uiState = UiState.Error(
                    e.toUiMessage(),
                    e.toTextDescription()
                )
            } catch (e: Error) {
                uiState = UiState.Error(
                    R.string.unknown_error,
                    """
                        Non-SongDataIOException exception thrown:
                        Type: ${e.javaClass.name}
                        ${e.message}
                    """.trimIndent()
                )
                Log.e("SearchViewModel", "Unknown error", e)
            }

        }
    }

}