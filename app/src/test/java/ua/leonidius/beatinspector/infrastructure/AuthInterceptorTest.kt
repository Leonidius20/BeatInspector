package ua.leonidius.beatinspector.infrastructure

import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.assertThrows
import org.junit.Test
import ua.leonidius.beatinspector.data.auth.logic.AuthTokenProvider
import ua.leonidius.beatinspector.data.auth.logic.TokenRefreshException
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException

class AuthInterceptorTest {

    abstract class YesFakeAuthTokenProvider: AuthTokenProvider {
        override fun isAuthorized() = true
    }

    object TrowingFakeAuthTokenProvider: YesFakeAuthTokenProvider() {

        val error = Error("test")
        override suspend fun getAccessToken() = throw TokenRefreshException("", "", error)
    }

    @Test
    fun testAddingTokenHeaderIfLoggedIn() {
        // todo: replace with Hilt injection so that constructor changes don't affect the test
        // val interceptor = AuthInterceptor(FakeAuthTokenProvider)

    }

    @Test
    fun testThrowingExceptionIfTokenRefreshFails() {
        val interceptor = AuthInterceptor(TrowingFakeAuthTokenProvider)

        val fakeInterceptorChain = mockk<Interceptor.Chain> {
            every { request() } returns mockk<Request>()
        }

        assertThrows(SongDataIOException.TokenRefresh::class.java) {
            interceptor.intercept(fakeInterceptorChain)
        }
    }

}