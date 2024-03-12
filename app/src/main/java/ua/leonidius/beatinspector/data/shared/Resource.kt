package ua.leonidius.beatinspector.data.shared

/**
 * A wrapper for a cacheable resource
 */
sealed class Resource<T> {

    data class Error<T> (
        val error: Throwable
    ): Resource<T>()

    sealed class Value<T>(
        val value: T
    ): Resource<T>()

    class Success<T>(
        value: T
    ): Value<T>(value)

    /**
     * Maybe something was loaded only partially, or only
     * the cached version is available
     */
    class ValueWithError<T>(
        value: T,
        val error: Throwable
    ): Value<T>(value)

    class Loading<T>: Resource<T>()

}