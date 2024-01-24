package ua.leonidius.beatinspector

class SongDataIOException(
    val type: Type,
    message: String? = null,
    cause: Throwable? = null
): Exception() {

    enum class Type {
        SERVER, NETWORK, UNKNOWN, OTHER
    }

}