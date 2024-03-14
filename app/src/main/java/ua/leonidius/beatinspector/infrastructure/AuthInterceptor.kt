package ua.leonidius.beatinspector.infrastructure

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ua.leonidius.beatinspector.data.auth.logic.AuthTokenProvider
import ua.leonidius.beatinspector.data.auth.logic.TokenRefreshException
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authenticator: AuthTokenProvider
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val authed = authenticator.isAuthorized()

        if (!authed) {
            throw SongDataIOException.NotLoggedIn
        } else {

            val accessToken = try {
                runBlocking {
                    authenticator.getAccessToken()
                }
            } catch (e: TokenRefreshException) {
                throw SongDataIOException.TokenRefresh(e)
            }

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