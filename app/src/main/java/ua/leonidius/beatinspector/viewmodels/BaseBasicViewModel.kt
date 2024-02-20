package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.repos.BasicRepository

/**
 * @param P - presentation layer object
 */
open class BaseBasicViewModel<I, T, P> (
    private val repo: BasicRepository<I, T>,
    private val getId: () -> I,
    private val mapper: (T) -> P
): ViewModel() {

    var uiState: UiState by mutableStateOf(UiState.Loading)
        protected set

    protected fun load() {
        viewModelScope.launch {
            try {
                val result = repo.get(getId())
                val presentationObject = mapper(result)
                uiState = UiState.Loaded(presentationObject)
            } catch (e: SongDataIOException) {
                uiState = UiState.Error(
                    e.toUiMessage(),
                    e.toTextDescription()
                )
            } catch (e: Exception) {
                uiState = UiState.Error(
                    R.string.unknown_error,
                    """
                        Non-SongDataIOException exception thrown:
                        Type: ${e.javaClass.name}
                        ${e.message}
                    """.trimIndent()
                )
            }
        }
    }

    sealed class UiState {

        object Loading : UiState()

        data class Loaded<P>(
            val presentationObject: P,
        ) : UiState()

        data class Error(
            val errorMessageId: Int,
            val errorAdditionalInfo: String,
        ) : UiState()

    }

}