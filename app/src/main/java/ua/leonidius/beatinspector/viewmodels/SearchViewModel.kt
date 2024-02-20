package ua.leonidius.beatinspector.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.search.SearchRepository
import ua.leonidius.beatinspector.repos.search.SearchRepositoryImpl
import ua.leonidius.beatinspector.repos.account.AccountRepository

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val accountRepository: AccountRepository,
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

    sealed class AccountImageState {

        data class Loaded(
            val imageUrl: String
        ) : AccountImageState()

        sealed class NotLoaded : AccountImageState()

        object ErrorLoading : NotLoaded()

        object Loading : NotLoaded()

        object NoImageOnAccount : NotLoaded()
    }

    var query by mutableStateOf("")

    var uiState by mutableStateOf<UiState>(UiState.Uninitialized)
        private set

    var accountImageState by mutableStateOf<AccountImageState>(AccountImageState.Loading)
        private set


    init {
        loadAccountImage()
    }

    private fun loadAccountImage() {
        viewModelScope.launch {
            accountImageState = AccountImageState.Loading

            accountImageState = try {
                val accountImageUrl = accountRepository.get(Unit).smallImageUrl
                if (accountImageUrl != null) {
                    AccountImageState.Loaded(accountImageUrl)
                } else {
                    AccountImageState.NoImageOnAccount
                }
            } catch (e: Exception) {
                AccountImageState.ErrorLoading
            }
        }
    }

    ///init {
    //    searchQueriesFlow.collect { query ->
            // lauch a coroutine to perform search
            // cancel previous request if it is still running
            // should we be cancelling a coroutine ?
            // no, only not letting next requests happen,
            // but how do we know that the prev is still running?
            // how do we combine network coroutines with this flow though
     ///   }
    //}

    fun performSearch() {
        //searchQueriesFlow.emit(query)

        viewModelScope.launch {
            try {
                uiState = UiState.Loading
                val results = searchRepository.get(query)
                uiState = UiState.Loaded(results)
            } catch (e: SearchRepositoryImpl.NotAuthedError) {
                // todo: check how that is thrown, handle it differently so that it redirects to login page
                // todo: set this to AuthStatusViewModel and initiate login

                uiState = UiState.Error(
                    R.string.other_error,
                    """
                        NotAuthedError thrown:
                        Message: ${e.message}
                        Try logging out (or clearing app data) and logging in again.
                    """.trimIndent()
                )
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

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[APPLICATION_KEY]) as BeatInspectorApp

                return SearchViewModel(
                    app.searchRepository,
                    app.accountRepository,
                ) as T
            }

        }

    }


}