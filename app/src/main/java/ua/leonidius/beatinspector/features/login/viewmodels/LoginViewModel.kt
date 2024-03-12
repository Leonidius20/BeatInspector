package ua.leonidius.beatinspector.features.login.viewmodels

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.data.auth.logic.Authenticator
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserHideExplicitSettingChangeEvent
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticator: Authenticator,
    private val eventBus: EventBus,
): ViewModel() {

    sealed class UiState {
        object LoginInProgress: UiState()

        object LoginOffered: UiState()

        data class LoginError(
            val errorDescription: String
        ): UiState()

        object SuccessfulLogin: UiState()
    }

    private val initialState = if (!authenticator.isAuthorized())
        UiState.LoginOffered
    else
        UiState.SuccessfulLogin

    var uiState by mutableStateOf(initialState)
        private set

    // todo: single source of truth for auth status - authenticator class. maybe make it a stateflow?
    //var isLoggedIn by mutableStateOf(authenticator.isAuthorized())
    //    private set

    /*fun checkAuthStatus(launchLoginActivityWithIntent: (Intent) -> Unit) {
        if (authenticator.isAuthorized()) { // todo: remove this shit?
            // logged in, do nothing
        } else {
            // todo: find a proper place for this code, maybe in some lifecycle observer
            val intent = authenticator.prepareStepOneIntent()
            launchLoginActivityWithIntent(intent)
        }
    }*/

    var iAmAMinorOptionSelected by mutableStateOf(false)

    fun launchLoginSequence(launchLoginActivityWithIntent: (Intent) -> Unit) {
        uiState = UiState.LoginInProgress

        eventBus.post(
            UserHideExplicitSettingChangeEvent(iAmAMinorOptionSelected),
            viewModelScope
        )

        val intent = authenticator.prepareStepOneIntent()
        launchLoginActivityWithIntent(intent)
    }

    fun onLoginActivityResult(activitySuccess: Boolean, data: Intent?) {
        if (!activitySuccess) {
            uiState = UiState.LoginError(
                errorDescription = "Error while logging in. Please try again."
            )
            return
        }

        // the code is obtained and now we need to exchange it for tokens to complete auth.
        // call the token-getting method
        viewModelScope.launch(Dispatchers.IO) { // todo: withContext() in  authenticator.authSecondStep(), not here
            /*authenticator.authSecondStep(data) { isSuccessful ->
                // todo: remove when auth becomes the SSOT?

                if (isSuccessful) {
                    uiState = UiState.SuccessfulLoginAccountDataLoading

                    // todo: start loading account data
                    //val accountData = accountDataCache.retrieve()
                    // accountDataCache.store()

                } else {
                    uiState = UiState.LoginError(
                        errorDescription = "Error while logging in. Please try again."
                    )
                }
            }*/

            try {
                authenticator.exchangeCodeForTokens(data)
            } catch (e: Exception) {
                uiState = UiState.LoginError(
                    errorDescription = "Error while logging in. Please try again. (${e.message})"
                )
                return@launch
            }


            uiState = UiState.SuccessfulLogin
        }
    }

    // this is supposed to be a viewmodel for the MainActivity. it should control
    // auth and redirect user to auth page if they are not authed.


    // based on the state in this viewmodel (authed = true or authed = false) we
    // show either the auth screen or whatever else

    fun logout() {
        eventBus.post(UserLogoutRequestEvent, viewModelScope)

        uiState = UiState.LoginOffered
    }


    /*companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return LoginViewModel(
                    app.authenticator,
                    app.eventBus,
                ) as T
            }

        }

    }*/

}