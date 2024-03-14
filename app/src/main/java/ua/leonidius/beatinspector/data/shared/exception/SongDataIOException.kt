package ua.leonidius.beatinspector.data.shared.exception

import java.io.IOException

sealed class SongDataIOException(
    cause: Throwable? = null
): IOException(cause) {

    abstract fun toTextDescription(): String

    data class Server(
        val code: Int?,
        val messageFromApi: String,
        val rawResponse: String? = null
    ) : SongDataIOException() {

        override fun toTextDescription() = """
            Code: $code
            Message from API: $messageFromApi
            Raw response: $rawResponse
        """.trimIndent()

    }

    data class Network(
        val e: Throwable
    ): SongDataIOException(e) {

        override fun toTextDescription() = """
            Exception type: ${e::class.java.name}
            Exception message: ${e.message}
            ${if (e is TokenRefresh) """
                Token refresh exception data:
                ${e.toTextDescription()}
            """.trimIndent() else ""}
        """.trimIndent()

    }

    data class Unknown(
        val e: Throwable,
        val rawResponse: String? = null
    ): SongDataIOException(e) {

        override fun toTextDescription() = """
            Exception type: ${e::class.java.name}
            Exception message: ${e.message}
            Raw API response: $rawResponse
        """.trimIndent()

    }

    /*data class Other(
        val messageFromLibrary: String,
        val e: Throwable
    ): SongDataIOException(e) {

        override fun toTextDescription() = """
            Message from library: $messageFromLibrary
            Exception message: ${e.message}
        """.trimIndent()

    }*/

    data class TokenRefresh(
        val e: Throwable // (TokenRefreshException)
    ): SongDataIOException() {

        override fun toTextDescription() = e.message!!

    }

    object NotLoggedIn: SongDataIOException() {

        override fun toTextDescription() = "User is not logged in. Please relaunch the app to trigger the login sequence. If that doesn't work, clear app data and try again."
    }

}