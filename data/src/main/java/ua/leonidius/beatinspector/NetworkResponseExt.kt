package ua.leonidius.beatinspector

import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.services.SpotifyRetrofitClient

fun <S> NetworkResponse.Error<S, SpotifyRetrofitClient.SpotifyError>.toUIException(): SongDataIOException {
    return when (this) {
        is NetworkResponse.ServerError -> {
            SongDataIOException.Server(
                this.code, this.body?.message ?: "< No response body >",
                /*this.response?.raw()?.body?.string()*/"extraction not implemented")
        }
        is NetworkResponse.NetworkError -> {
            SongDataIOException.Network(this.error)
        }
        is NetworkResponse.UnknownError -> {
            SongDataIOException.Unknown(
                this.error, /*this.response?.raw()?.body?.string()*/"extraction not implemented")
        }
    }
}