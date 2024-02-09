package ua.leonidius.beatinspector

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
        val code: Int,
        val errorFromLibrary: String?,
        val errorDescriptionFromLibrary: String?,
        val e: Throwable
    ): SongDataIOException() {

        override fun toTextDescription() = """
            Code: $code
            Error from library: $errorFromLibrary
            Error description from library: $errorDescriptionFromLibrary
            Exception (cause) type: ${e::class.java.name}
            Exception (cause) message: ${e.message}
        """.trimIndent()

    }

}