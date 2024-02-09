package ua.leonidius.beatinspector.repos.retrofit

import okhttp3.Interceptor
import okio.Buffer

class SaveRawResponseInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val response = chain.proceed(chain.request())

        val buffer = response.peekBody(Long.MAX_VALUE)
        // decode as utf-8 string
        val rawResponse = buffer.string()


        return response
    }

}