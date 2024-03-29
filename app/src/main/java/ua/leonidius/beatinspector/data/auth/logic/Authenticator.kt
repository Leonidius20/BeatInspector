package ua.leonidius.beatinspector.data.auth.logic

import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import ua.leonidius.beatinspector.data.auth.storage.AuthStateSharedPrefStorage
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class Authenticator @Inject constructor(
    @Named("client_id") private val clientId: String,
    private val authStateStorage: AuthStateSharedPrefStorage,
   // private val authStateFlowingStorage: AuthStateFlowingStorage,
    private val authService: AuthorizationService,
    eventBus: EventBus,
): AuthTokenProvider, PKCEAuthenticationInitiator {

    init {
        eventBus.subscribe(UserLogoutRequestEvent::class) {
            logout()
        }
    }

    private val authServiceConfig: AuthorizationServiceConfiguration = // todo: replace by "fetchfromissuer" async
        AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/oauth2/v2/auth"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )

    private var authState: AuthState = if (!authStateStorage.isAuthStateStored()) {
        AuthState(authServiceConfig)
    } else {
        try {
            AuthState.jsonDeserialize(authStateStorage.getJson())
        } catch (e: Error) {
            AuthState(authServiceConfig)
        }
    }

    private fun isTokenRefreshNeeded(): Boolean {
        return authState.needsTokenRefresh
    }

    override suspend fun getAccessToken(): String {
        if (isTokenRefreshNeeded()) {
            refreshTokens()
        }
        return authState.accessToken!!
    }

    //val authStateFlow = MutableSharedFlow<AuthState>()

    init {
        /*authStateFlowingStorage.jsonAuthStateFlow
            .map { json ->
                if (json == null) {
                    AuthState(authServiceConfig)
                } else {
                    try {
                        AuthState.jsonDeserialize(json)
                    } catch (e: Error) {
                        AuthState(authServiceConfig)
                    }
                }
            }
            .onEach { authState = it }
            .onEach { authStateFlow.emit(it) }
            .catch { e -> Log.e("Authenticator", "authStateFlow error", e) }*/
    }

    override fun prepareStepOneIntent(): Intent {
        val authRequest = AuthorizationRequest.Builder(
            authServiceConfig,
            clientId,
            ResponseTypeValues.CODE,
            Uri.parse("ua.leonidius.beatinspector://login-callback") // todo: redirect uri
        )
            /*
             * user-library-read: view saved tracks
             * playlist-read-private: view private playlists
             * playlist-read-collaborative: view collaborative playlists
             * user-read-recently-played: view recently played tracks
             * user-top-read: view top tracks
             */
            .setScope("user-library-read playlist-read-private user-read-recently-played user-top-read")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    /**
     * @param callback what to do after onResponse based on whether the
     * auth succeeded
     */
    /*suspend fun authSecondStep(intent: Intent?, callback: (Boolean) -> Unit) {
        val resp = AuthorizationResponse.fromIntent(intent!!)
        val ex = AuthorizationException.fromIntent(intent)

        authState.update(resp, ex)

        val redirectUri = intent.data


        if (resp != null) {
            // success getting code

            authService.performTokenRequest(
                resp.createTokenExchangeRequest(),
            ) { tokenResp, authException ->
                if (tokenResp != null) {
                    // success
                    authState.update(tokenResp, authException)


                    storeAuthState()

                    callback(true)
                } else {
                    // fail
                    authException!!.printStackTrace()
                    callback(false)
                }
            }

        } else {
            // error - check exception
            ex!!.printStackTrace()
            callback(false)
        }
    }*/

    override suspend fun exchangeCodeForTokens(prevStepIntent: Intent?) = suspendCoroutine {
        val resp = AuthorizationResponse.fromIntent(prevStepIntent!!)
        val ex = AuthorizationException.fromIntent(prevStepIntent)

        authState.update(resp, ex)

        if (resp == null) {
            it.resumeWithException(ex ?: Error("Authenticator: Unknown error when obtaining code before exchanging code for tokens"))
            return@suspendCoroutine
        }

        authService.performTokenRequest(
            resp.createTokenExchangeRequest(),
        ) { tokenResp, authException ->
            authState.update(tokenResp, authException)

            if (tokenResp == null) {
                it.resumeWithException(authException ?: Error("Authenticator: Unknown error when exchanging code for tokens"))
            } else {

                storeAuthState()
                it.resume(Unit)
            }
        }
    }

    private fun storeAuthState() {
        authStateStorage.storeJson(authState.jsonSerializeString())
        /*if (authState.isAuthorized) {
            _loginState.value = LoginState.LoggedIn
        } else {
            _loginState.value = LoginState.NotLoggedIn
        }*/
    }

    override fun isAuthorized() = authState.isAuthorized

    /**
     * Refresh and store new tokens. This includes refresh token rotation.
     * We can't just use the performWithFreshTokens() method because
     * it doesn't allow us to rotate refresh tokens themselves too.
     * So instead, we use this method when refresh is needed.
     */
    private suspend fun refreshTokens() = suspendCoroutine {
        authService.performTokenRequest(
            authState.createTokenRefreshRequest()
        ) { tokenResp, authException ->
            authState.update(tokenResp, authException)

            if (authException != null || tokenResp == null) {
                // fail
                Log.e("Authenticator", "refreshTokensBlocking: token refresh failed", authException)
                it.resumeWithException(authException!!.toDataLayerException())
            } else {
                // success
                storeAuthState()
                it.resume(Unit)
            }
        }
    }

    private fun logout() {
        authState = AuthState(authServiceConfig)
        authStateStorage.clear()

        // todo: turn authstate itself into a flow
        _loginState.value = LoginState.NotLoggedIn
    }

    private val _loginState = MutableStateFlow(
        if (authState.isAuthorized)
            LoginState.LoggedIn
        else LoginState.NotLoggedIn
    )

    /*override val loginState: StateFlow<LoginState>
        get() = _loginState.asStateFlow()*/

}

private fun AuthorizationException.toDataLayerException() =
    TokenRefreshException(
        error ?: "No error provided",
        errorDescription ?: "No description provided",
        this
    )