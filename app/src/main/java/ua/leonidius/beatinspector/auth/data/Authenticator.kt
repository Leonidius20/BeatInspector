package ua.leonidius.beatinspector.auth.data

import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import ua.leonidius.beatinspector.auth.AuthStateStorage
import ua.leonidius.beatinspector.shared.eventbus.Event
import ua.leonidius.beatinspector.shared.eventbus.UserLogoutRequestEvent

class Authenticator(
    private val clientId: String,
    private val authStateStorage: AuthStateStorage,
   // private val authStateFlowingStorage: AuthStateFlowingStorage,
    private val authService: AuthorizationService,
    private val eventBus: Flow<Event>,
    private val eventCollectScope: CoroutineScope = MainScope(),
) {

    //private val logoutObservers = mutableListOf<() -> Unit>() // todo: replace with event bus

    //fun addLogoutObserver(observer: () -> Unit) {
   //     logoutObservers.add(observer)
    //}


    init {
        eventCollectScope.launch {
            eventBus
                .filterIsInstance<UserLogoutRequestEvent>()
                .collect {
                    logout()
                }
        }
    }

    // todo
    init {
        // eventBus.filterIsInstance<UserLogoutEvent>()
    //
    // .collectLatest { event ->
        //     if (event is UserLogoutEvent) {
        //         logout()
        //     }
        // }
    }

    private val authServiceConfig: AuthorizationServiceConfiguration = // todo: replace by "fetchfromissuer" async
        AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/oauth2/v2/auth"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )

    var authState: AuthState = if (!authStateStorage.isAuthStateStored()) {
        AuthState(authServiceConfig)
    } else {
        try {
            AuthState.jsonDeserialize(authStateStorage.getJson())
        } catch (e: Error) {
            AuthState(authServiceConfig)
        }
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

    fun prepareStepOneIntent(): Intent {
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

    suspend fun exchangeCodeForTokens(prevStepIntent: Intent?) = suspendCoroutine {
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
            if (tokenResp == null) {
                it.resumeWithException(authException ?: Error("Authenticator: Unknown error when exchanging code for tokens"))
            } else {
                authState.update(tokenResp, authException)
                storeAuthState()
                it.resume(Unit)
            }
        }
    }

    private fun storeAuthState() {
        authStateStorage.storeJson(authState.jsonSerializeString())
    }

    fun isAuthorized() = authState.isAuthorized

    fun refreshTokensBlocking() {
        // this also includes refresh token rotation
        /*
         * we can't just use the performWithFreshTokens method because
         * it doesn't allow us to get the new refresh token
         * under the "refresh token rotation" scheme
         */
       // if (!authState.needsTokenRefresh) return true

        // todo: use CallbackFlow here? or countDownLatch?

        // val promise = CompletableFuture<Boolean>()

        val latch = CountDownLatch(1)

        var exception: AuthorizationException? = null


        authService.performTokenRequest(
            authState.createTokenRefreshRequest()
        ) { tokenResp, authException ->
            authState.update(tokenResp, authException)

            if (authException != null || tokenResp == null) {
                // fail
                exception = authException
                Log.e("Authenticator", "refreshTokensBlocking: token refresh failed", authException)
                latch.countDown()
                // promise.complete(false)
            } else {
                // success
                // promise.complete(true)
                storeAuthState()
                latch.countDown()
            }
        }

        latch.await()

        if (exception != null) {
            throw exception!!
        }
    }

    private fun logout() {
        authState = AuthState(authServiceConfig)
        authStateStorage.clear()
        // logoutObservers.forEach { it() }
    }

}