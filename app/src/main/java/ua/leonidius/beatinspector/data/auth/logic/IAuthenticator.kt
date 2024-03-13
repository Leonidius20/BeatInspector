package ua.leonidius.beatinspector.data.auth.logic

import kotlinx.coroutines.flow.StateFlow

interface IAuthenticator {

    // val loginState: StateFlow<LoginState>

    fun isTokenRefreshNeeded(): Boolean

    suspend fun refreshTokens()

    fun getAccessToken(): String

    fun isAuthorized(): Boolean
}

sealed class LoginState {
    object NotLoggedIn : LoginState()
    object LoggedIn : LoginState()
}