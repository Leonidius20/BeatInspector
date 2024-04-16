package ua.leonidius.beatinspector.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * When the API usage is blocked, it does not return a JSON result,
 * instead it returns an empty ? or html? response. This makes
 * GsonConverter crash. This interceptor should catch such cases
 * and throw a custom io exception.
 */
@Singleton
class ApiErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 403) {
            throw SongDataIOException.ApiAccessDenied
        }

        return response
    }


}