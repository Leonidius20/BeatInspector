package ua.leonidius.beatinspector.repos.retrofit

import android.util.Log
import net.openid.appauth.AuthorizationException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.auth.Authenticator

class AuthInterceptor(private val authenticator: Authenticator): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val authed = authenticator.authState.isAuthorized

        if (!authed) {
            return Response.Builder()
                .code(418) // teapot code
                .body(
                    ResponseBody.create(
                        "text/html; charset=utf-8".toMediaType(),
                        ""
                    )
                ) // Whatever body
                .protocol(Protocol.HTTP_2)
                .message("Dummy response")
                .request(chain.request())
                .build()
        } else {
            val needsRefresh = authenticator.authState.needsTokenRefresh

            if (needsRefresh) {

                try {
                    authenticator.refreshTokensBlocking()
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

            val accessToken = authenticator.authState.accessToken

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