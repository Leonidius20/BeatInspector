package ua.leonidius.beatinspector.shared.uimapping

import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.R

fun SongDataIOException.toUiMessage(): Int {
    return when (this) {
        is SongDataIOException.Network -> R.string.network_error
        is SongDataIOException.Server -> R.string.server_error
        is SongDataIOException.Unknown -> R.string.unknown_error
        is SongDataIOException.TokenRefresh -> R.string.token_refresh_error
    }
}