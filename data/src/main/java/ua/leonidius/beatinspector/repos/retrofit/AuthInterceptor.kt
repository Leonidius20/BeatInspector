package ua.leonidius.beatinspector.repos.retrofit

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import ua.leonidius.beatinspector.auth.Authenticator

class AuthInterceptor(val authenticator: Authenticator): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val authed = authenticator.authState.isAuthorized

        if (!authed) {
            return Response.Builder()
                .code(418) // teapot code
                .body(ResponseBody.create(MediaType.get("text/html; charset=utf-8"), "")) // Whatever body
                .protocol(Protocol.HTTP_2)
                .message("Dummy response")
                .request(chain.request())
                .build()
        } else {
            val needsRefresh = authenticator.authState.needsTokenRefresh

            if (needsRefresh) {

                val success = authenticator.refreshTokensBlocking()

                if (!success) {
                    throw Error("Failed to refresh tokens")
                }
            }

            val accessToken = authenticator.authState.accessToken

            val request = original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .method(original.method(), original.body())
                .build()

            // this is a workaround for a bug in AppAuth library
            // which forces this callback to be executed on the
            // main thread after a token refresh. this causes
            // okhttp3 to throw an exception

            return chain.proceed(request)


        }

    }

}