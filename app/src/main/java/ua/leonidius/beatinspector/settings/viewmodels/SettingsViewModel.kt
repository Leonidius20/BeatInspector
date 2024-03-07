package ua.leonidius.beatinspector.settings.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.mikepenz.aboutlibraries.entity.Library
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.entities.AccountDetails
import ua.leonidius.beatinspector.repos.BasicRepository
import ua.leonidius.beatinspector.settings.data.SettingsStore
import ua.leonidius.beatinspector.shared.eventbus.Event
import ua.leonidius.beatinspector.shared.eventbus.UserHideExplicitSettingChangeEvent

class SettingsViewModel(
    private val accountRepository: BasicRepository<Unit, AccountDetails>,
    private val libraries: List<Library>,
    private val settingsStore: SettingsStore,
    private val eventBus: MutableSharedFlow<Event>,
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

    val libraryNameAndLicenseHash = libraries.map {
        Pair(it.name, it.licenses.firstOrNull()?.hash)
    }.toTypedArray()

    val hideExplicit = settingsStore.hideExplicitFlow

    init {
        loadAccountDetails()
    }

    private fun loadAccountDetails() {
        accountDetailsState = AccountDetailsState.Loading
        viewModelScope.launch {
            accountDetailsState = try {
                val details = accountRepository.get(Unit)
                AccountDetailsState.Loaded(
                    details.username, details.bigImageUrl,
                )

            } catch (e: Exception) {
                // todo: better error handling
                AccountDetailsState.Error
            }

        }
    }

    fun toggleHideExplicit(value: Boolean) {
        viewModelScope.launch {
            // todo: replace with factory to remove dependency on event constructor
            eventBus.emit(UserHideExplicitSettingChangeEvent(value))
        }
    }


    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return SettingsViewModel(
                    app.accountRepository,
                    app.libraries,
                    app.settingsStore,
                    app.eventBus,
                ) as T
            }

        }

    }


}