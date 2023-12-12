package ua.leonidius.beatinspector.repos.retrofit

import android.content.Context
import android.net.Uri
import android.util.Base64
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import ua.leonidius.beatinspector.auth.Authenticator
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom

class AuthInterceptor(val authenticator: Authenticator): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val authed = authenticator.accessToken != ""

        if (!authed) {
            return Response.Builder()
                .code(418) // teapot code
                .body(ResponseBody.create(MediaType.get("text/html; charset=utf-8"), "")) // Whatever body
                .protocol(Protocol.HTTP_2)
                .message("Dummy response")
                .request(chain.request())
                .build();
        } else {
            val token = authenticator.accessToken


            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .method(original.method(), original.body())
                .build()

            return chain.proceed(request)
        }

    }

    private fun generateVerifier(): String {
        val secureRandom = SecureRandom()
        val code = ByteArray(32)
        secureRandom.nextBytes(code)
        return Base64.encodeToString(code,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charset.forName("US-ASCII"))
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digestBytes = messageDigest.digest()
        return Base64.encodeToString(digestBytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

}