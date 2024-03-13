package ua.leonidius.beatinspector.data.auth.logic

import android.content.Intent
import kotlinx.coroutines.flow.StateFlow

interface IAuthenticator {

    // val loginState: StateFlow<LoginState>

    fun isTokenRefreshNeeded(): Boolean

    suspend fun refreshTokens()

    fun getAccessToken(): String

    fun isAuthorized(): Boolean

    fun prepareStepOneIntent(): Intent

    suspend fun exchangeCodeForTokens(prevStepIntent: Intent?)
}

sealed class LoginState {
    object NotLoggedIn : LoginState()
    object LoggedIn : LoginState()
}