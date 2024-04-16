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
import ua.leonidius.beatinspector.data.auth.logic.PKCEAuthenticationInitiator
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserHideExplicitSettingChangeEvent
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticator: PKCEAuthenticationInitiator,
    private val eventBus: EventBus,
): ViewModel() {

    sealed class UiState {
        object LoginInProgress: UiState()

        object LoginOffered: UiState()

        data class LoginError(
            val errorDescription: String
        ): UiState()

        object SuccessfulLogin: UiState() // todo: find a way to navigate to the main screen after login without having to use this state, and remove it
    }

    var uiState by mutableStateOf<UiState>(UiState.LoginOffered)
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
                // todo: remove when the auth state is converted to flow
                uiState = UiState.SuccessfulLogin
            } catch (e: Exception) {
                uiState = UiState.LoginError(
                    errorDescription = "Error while logging in. Please try again. (${e.message})"
                )
                return@launch
            }


            //uiState = UiState.SuccessfulLogin
        }
    }

    fun logout() {
        eventBus.post(UserLogoutRequestEvent, viewModelScope)

        uiState = UiState.LoginOffered
    }

}