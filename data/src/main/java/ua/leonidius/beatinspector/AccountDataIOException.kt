package ua.leonidius.beatinspector

import java.io.IOException

class AccountDataIOException(
    throwable: Throwable? = null,
): IOException(throwable) {
}