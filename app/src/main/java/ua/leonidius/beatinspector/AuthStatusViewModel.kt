package ua.leonidius.beatinspector

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthStatusViewModel: ViewModel() {

    private var _isLoggedIn by mutableStateOf(false)

    val isLoggedIn
            get() = _isLoggedIn

    fun initiateLogin(context: Context) {
        viewModelScope.launch {
            delay(5000)
            _isLoggedIn = true
        }
    }

    // this is supposed to be a viewmodel for the MainActivity. it should control
    // auth and redirect user to auth page if they are not authed.


    // based on the state in this viewmodel (authed = true or authed = false) we
    // show either the auth screen or whatever else

    // we can assume authed= true, only set it to false after the first failed request

}