package ua.leonidius.beatinspector.repos.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class FixCacheControlInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .header("Cache-Control", "max-age=7200")
            .build()
    }

}