package ua.leonidius.beatinspector.repos

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.leonidius.beatinspector.domain.entities.SongSearchResult
import ua.leonidius.beatinspector.domain.repositories.SongsRepository
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient
import ua.leonidius.beatinspector.repos.retrofit.SpotifySearchResult


class SongsRepositoryImpl(
    val spotifyRetrofitClient: SpotifyRetrofitClient
) : SongsRepository {

    override suspend fun searchForSongsByTitle(q: String): List<SongSearchResult> {
        val result = spotifyRetrofitClient.searchForSongs(q)
        if (!result.isSuccessful) {
            throw Error("error when doing the request" + result.errorBody()!!.string())
            // todo: proper error handling
        } else {
            if (result.code() == 418) {
                // no stored token found
                throw NotAuthedError()
            } else {
                return result.body()!!.tracks.items.map {
                    SongSearchResult(it.id, it.name, it.artistsListToString())
                }
            }


        }
    }

    class NotAuthedError: Error()

}