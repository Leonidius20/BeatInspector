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
import ua.leonidius.beatinspector.repos.account.AccountRepository

class SettingsViewModel(
    private val accountRepository: AccountRepository,
): ViewModel() {

    sealed class AccountDetailsState {

        object Loading: AccountDetailsState()

        data class Loaded(
            val username: String,
            val bigImageUrl: String?,
        ) : AccountDetailsState()

        object Error : AccountDetailsState()

    }

    var accountDetailsState by mutableStateOf<AccountDetailsState>(AccountDetailsState.Loading)
        private set

    init {
        loadAccountDetails()
    }

    private fun loadAccountDetails() {
        accountDetailsState = AccountDetailsState.Loading
        viewModelScope.launch {
            accountDetailsState = try {
                val details = accountRepository.getAccountDetails()
                AccountDetailsState.Loaded(
                    details.username, details.bigImageUrl,
                )

            } catch (e: Exception) {
                // todo: better error handling
                AccountDetailsState.Error
            }

        }
    }


    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return SettingsViewModel(
                    app.accountRepository,
                ) as T
            }

        }

    }


}