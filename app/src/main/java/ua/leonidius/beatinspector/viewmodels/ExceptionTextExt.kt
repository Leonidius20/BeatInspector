package ua.leonidius.beatinspector.viewmodels

import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.R

fun SongDataIOException.toUiMessage(): Int {
    return when (type) {
        SongDataIOException.Type.NETWORK -> R.string.network_error
        SongDataIOException.Type.SERVER -> R.string.server_error
        SongDataIOException.Type.UNKNOWN -> R.string.unknown_error
        SongDataIOException.Type.OTHER -> R.string.other_error
    }
}