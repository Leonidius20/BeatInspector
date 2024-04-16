package ua.leonidius.beatinspector.features.shared.model

import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.R

fun SongDataIOException.toUiMessage(): Int {
    return when (this) {
        is SongDataIOException.Network -> R.string.network_error
        is SongDataIOException.Server -> R.string.server_error
        is SongDataIOException.Unknown -> R.string.unknown_error
        is SongDataIOException.TokenRefresh -> R.string.token_refresh_error
        is SongDataIOException.NotLoggedIn -> R.string.not_logged_in_error
        is SongDataIOException.ApiAccessDenied -> R.string.api_access_denied_error
    }
}