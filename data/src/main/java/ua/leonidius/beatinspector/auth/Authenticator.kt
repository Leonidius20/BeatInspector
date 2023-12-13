package ua.leonidius.beatinspector.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import ua.leonidius.beatinspector.data.R
import java.util.concurrent.CompletableFuture

class Authenticator(
    val clientId: String,
    val appContext: Context,
) {

    val prefs = appContext.getSharedPreferences(appContext.getString(
        R.string.preferences_tokens_file_name
    ), Context.MODE_PRIVATE)

    private val authStateJson: String
        get() = prefs.getString(
            PREF_KEY_AUTH_STATE, "") ?: ""

    companion object {
        const val RC_AUTH = 1
        const val PREF_KEY_AUTH_STATE = "auth_state"
    }

    private val authServiceConfig: AuthorizationServiceConfiguration = // todo: replace by "fetchfromissuer" async
        AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/oauth2/v2/auth"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )

    val authState: AuthState = if (authStateJson == "") {
        AuthState(authServiceConfig)
    } else {
        try {
            AuthState.jsonDeserialize(authStateJson)
        } catch (e: Error) {
            AuthState(authServiceConfig)
        }
    }

    val authService = AuthorizationService(appContext)

    fun authenticate(
        context: ComponentActivity // activity can be destroyed, so that's why we inject context here and not in constructor
    ) {


        val authRequest = AuthorizationRequest.Builder(
           authServiceConfig,
           clientId,
           ResponseTypeValues.CODE,
           Uri.parse("ua.leonidius.beatinspector://login-callback") // todo: redirect uri
       ).setScope("user-library-read playlist-read-private playlist-read-collaborative user-read-recently-played user-top-read")
           .build()


        val intent = authService.getAuthorizationRequestIntent(authRequest)

        context.startActivityForResult(intent, RC_AUTH)

    }

    /**
     * @param callback what to do after onResponse based on whether the
     * auth succeeded
     */
    fun onResponse(context: ComponentActivity, intent: Intent?, callback: (Boolean) -> Unit) {
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
    }

    fun storeAuthState() {
        with(prefs.edit()) {
            putString(PREF_KEY_AUTH_STATE, authState.jsonSerializeString())
            apply()
        }
    }

    fun isAuthorized() = authState.isAuthorized

    fun refreshTokensBlocking(): Boolean {
        // this also includes refresh token rotation
        /*
         * we can't just use the performWithFreshTokens method because
         * it doesn't allow us to get the new refresh token
         * under the "refresh token rotation" scheme
         */
       // if (!authState.needsTokenRefresh) return true

        val promise = CompletableFuture<Boolean>()

        authService.performTokenRequest(
            authState.createTokenRefreshRequest()
        ) { tokenResp, authException ->
            authState.update(tokenResp, authException)

            if (tokenResp != null) {
                // success
                promise.complete(true)
                storeAuthState()
            } else {
                // fail
                promise.complete(false)
                authException!!.printStackTrace()
            }
        }

        return promise.get()
    }

}