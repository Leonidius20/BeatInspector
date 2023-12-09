package ua.leonidius.beatinspector.repos.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        val original = chain!!.request()

        val token = ""
        // expires in 1 hour

        val request = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .method(original.method(), original.body())
            .build()

        return chain.proceed(request)
    }

}