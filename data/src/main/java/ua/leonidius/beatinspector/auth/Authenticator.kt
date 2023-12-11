package ua.leonidius.beatinspector.auth

import android.R.attr.data
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


class Authenticator(
    val clientId: String,
    val applicationContext: Context,
    var accessToken: String = "",
    var refreshToken: String = "",
) {

    companion object {
        const val RC_AUTH = 1
    }

    private val authServiceConfig: AuthorizationServiceConfiguration = // todo: replace by "fetchfromissuer" async
        AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.spotify.com/oauth2/v2/auth"),
            Uri.parse("https://accounts.spotify.com/api/token")
        )
    val authState: AuthState = AuthState(authServiceConfig)

    val authService = AuthorizationService(applicationContext)

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
                    accessToken = tokenResp.accessToken!!
                    refreshToken = tokenResp.refreshToken!!
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

}