package ua.leonidius.beatinspector

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.auth.Authenticator

class AuthStatusViewModel(val authenticator: Authenticator): ViewModel() {

    private var _isLoggedIn by mutableStateOf(false)

    val isLoggedIn
            get() = _isLoggedIn

    fun initiateLogin(context: ComponentActivity) {
        viewModelScope.launch {
            authenticator.authenticate(context)
        }
    }

    @Deprecated("temporary solution")
    fun setLoggedIn(boolean: Boolean) {
        _isLoggedIn = boolean
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