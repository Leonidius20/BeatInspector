package ua.leonidius.beatinspector

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.auth.logic.AuthTokenProvider
import javax.inject.Inject

/**
 * This viewmodel is designed to load the auth state on the launch
 * of the app to decide if login screen should be shown or not.
 * This is its only purpose. AuthStateViewModel will be renambed into
 * LoginViewModel and will only care about the ui state of the login
 * screen and submitting the results to the data layer. It will be in
 * features/login/viewmodels.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authenticator: AuthTokenProvider
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return authenticator.isAuthorized()
    }

}