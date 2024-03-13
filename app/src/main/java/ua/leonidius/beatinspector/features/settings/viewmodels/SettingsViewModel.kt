package ua.leonidius.beatinspector.features.settings.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikepenz.aboutlibraries.entity.Library
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import ua.leonidius.beatinspector.data.settings.SettingsRepository
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserHideExplicitSettingChangeEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val libraries: List<Library>,
    private val settingsStore: SettingsRepository,
    private val eventBus: EventBus,
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

    val hideExplicit = settingsStore.settingsFlow.map { it.hideExplicit }

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
        // todo: replace with factory to remove dependency on event constructor
        eventBus.post(UserHideExplicitSettingChangeEvent(value), viewModelScope)
    }


    /*companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return SettingsViewModel(
                    app.accountRepository,
                    app.libraries,
                    app.settingsStore,
                    app.eventBusO,
                ) as T
            }

        }

    }*/


}