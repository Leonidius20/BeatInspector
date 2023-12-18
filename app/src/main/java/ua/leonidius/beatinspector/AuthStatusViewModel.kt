package ua.leonidius.beatinspector

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.auth.Authenticator

class AuthStatusViewModel(private val authenticator: Authenticator): ViewModel() {

    // todo: single source of truth for auth status - authenticator class. maybe make it a stateflow?
    var isLoggedIn by mutableStateOf(authenticator.isAuthorized())
        private set

    fun initiateLogin(launchLoginActivityWithIntent: (Intent) -> Unit) {
        if (isLoggedIn) { // todo: remove this shit?
            // logged in, do nothing
        } else {
            // todo: find a proper place for this code, maybe in some lifecycle observer
            val intent = authenticator.prepareStepOneIntent()
            launchLoginActivityWithIntent(intent)
        }
    }

    fun onLoginActivityResult(activitySuccess: Boolean, data: Intent?) {
        if (activitySuccess) {
            // call the token-getting method
            viewModelScope.launch(Dispatchers.IO) {
                authenticator.authSecondStep(data) { isSuccessful ->
                    // todo: remove when auth becomes the SSOT?
                    // although we need error display
                    isLoggedIn = isSuccessful
                }
            }
        } else {
            // todo
        }
    }

    // this is supposed to be a viewmodel for the MainActivity. it should control
    // auth and redirect user to auth page if they are not authed.


    // based on the state in this viewmodel (authed = true or authed = false) we
    // show either the auth screen or whatever else

    // we can assume authed= true, only set it to false after the first failed request


    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                return AuthStatusViewModel(app.authenticator) as T
            }

        }

    }

}