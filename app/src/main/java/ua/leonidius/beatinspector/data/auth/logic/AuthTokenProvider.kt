package ua.leonidius.beatinspector.data.auth.logic

import android.content.Intent

/**
 * Provides a fresh auth token to be added to the request headers.
 * If the token is expired, it will be refreshed before being returned.
 */
interface AuthTokenProvider {

    // val loginState: StateFlow<LoginState>

    //fun isTokenRefreshNeeded(): Boolean

    //suspend fun refreshTokens()

    suspend fun getAccessToken(): String

    fun isAuthorized(): Boolean


}

/**
 * Provides methods to perform the two steps of the PKCE authentication flow:
 * 1. Getting the code
 * 2. Exchanging the code for tokens
 * It is expected that this class will also handle storing auth state after performing
 * these steps.
 */
interface PKCEAuthenticationInitiator {

    fun prepareStepOneIntent(): Intent

    suspend fun exchangeCodeForTokens(prevStepIntent: Intent?)

}

sealed class LoginState {
    object NotLoggedIn : LoginState()
    object LoggedIn : LoginState()
}