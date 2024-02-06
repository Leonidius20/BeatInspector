package ua.leonidius.beatinspector.repos.account

import java.io.IOException

class AccountDataIOException(
    throwable: Throwable? = null,
): IOException(throwable) {
}