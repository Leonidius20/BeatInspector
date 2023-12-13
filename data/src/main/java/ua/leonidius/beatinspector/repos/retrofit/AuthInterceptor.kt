package ua.leonidius.beatinspector.repos.retrofit

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import ua.leonidius.beatinspector.auth.Authenticator
import java.util.concurrent.CompletableFuture

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
            val promise = CompletableFuture<Response>()

            authenticator.authState.performActionWithFreshTokens(authenticator.authService) { accessToken, _, exception ->
                if (exception != null) {
                    throw Error("Failed to refresh token", exception)
                } else {
                    authenticator.storeAuthState()

                    val request = original.newBuilder()
                        .header("Authorization", "Bearer $accessToken")
                        .method(original.method(), original.body())
                        .build()

                    promise.complete(chain.proceed(request))
                }
            }

            return promise.get()
        }

    }

}