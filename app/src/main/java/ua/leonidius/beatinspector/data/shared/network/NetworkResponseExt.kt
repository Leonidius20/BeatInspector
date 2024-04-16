package ua.leonidius.beatinspector.data.shared.network

import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException

fun <S> NetworkResponse.Error<S, ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse>.toUIException(): SongDataIOException {
    if (this.error is SongDataIOException) {
        return this.error as SongDataIOException // todo: we should clearly separate infrastructure layer exceptions, like api access blocked, from the rest
    }


    return when (this) {
        is NetworkResponse.ServerError -> {
            SongDataIOException.Server(
                this.code, this.body?.message ?: "< No response body >",
                /*this.response?.raw()?.body?.string()*/"extraction not implemented"
            )
        }
        is NetworkResponse.NetworkError -> {
            SongDataIOException.Network(this.error)
        }
        is NetworkResponse.UnknownError -> {
            SongDataIOException.Unknown(
                this.error, /*this.response?.raw()?.body?.string()*/"extraction not implemented"
            )
        }
    }
}