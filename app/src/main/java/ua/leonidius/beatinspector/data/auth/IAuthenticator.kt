package ua.leonidius.beatinspector.data.auth

import kotlinx.coroutines.flow.StateFlow

interface IAuthenticator {

    val loginState: StateFlow<LoginState>

    fun isTokenRefreshNeeded(): Boolean

    suspend fun refreshTokens()

    fun getAccessToken(): String
}

sealed class LoginState {
    object NotLoggedIn : LoginState()
    object LoggedIn : LoginState()
}