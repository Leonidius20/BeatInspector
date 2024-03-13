package ua.leonidius.beatinspector.infrastructure

import android.util.Log
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationException
import okhttp3.Interceptor
import okhttp3.Response
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.data.auth.logic.IAuthenticator
import ua.leonidius.beatinspector.data.auth.logic.LoginState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authenticator: IAuthenticator
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val authed = authenticator.isAuthorized()

        if (!authed) {
            throw SongDataIOException.NotLoggedIn
        } else {
            val needsRefresh = authenticator.isTokenRefreshNeeded()

            if (needsRefresh) {

                try {
                    runBlocking {
                        authenticator.refreshTokens()
                    }

                    Log.d("AuthInterceptor", "Token refreshed")
                } catch (e: AuthorizationException) { // todo: i don't think its a good idea to depend on AppAuth library here, better create a wrapper exception class
                    throw SongDataIOException.TokenRefresh(
                        e.code,
                        e.error,
                        e.errorDescription,
                        e
                    )
                }
            }

            val accessToken = authenticator.getAccessToken()

            val request = original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .method(original.method, original.body)
                .build()

            // this is a workaround for a bug in AppAuth library
            // which forces this callback to be executed on the
            // main thread after a token refresh. this causes
            // okhttp3 to throw an exception

            return chain.proceed(request)


        }

    }

}