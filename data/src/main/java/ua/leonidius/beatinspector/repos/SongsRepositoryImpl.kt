package ua.leonidius.beatinspector.repos

import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSource
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class SongsRepositoryImpl(
    private val spotifyRetrofitClient: SpotifyRetrofitClient,
    private val inMemCache: SongsInMemCache,
    private val networkDataSource: SongsNetworkDataSource
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
                }.onEach { inMemCache.songSearchResults[it.id] = it }
            }


        }
    }

    class NotAuthedError: Error()


    override suspend fun getTrackDetails(id: String): Song {
        val baseInfo = inMemCache.songSearchResults[id]
            ?: throw Error("no base info found in cache for song id $id")

        var details = inMemCache.getSongDetailsById(id)

        if (details == null) {
            details = networkDataSource.getSongDetailsById(id)
            if (details == null) {
                throw Error("no details could be loaded from network for song id $id")
            } else {
                inMemCache.songsDetails[id] = details
            }
        }

        return Song(
            id = baseInfo.id,
            name = baseInfo.name,
            artist = baseInfo.artist,
            duration = details.duration,
            loudness = details.loudness,
            bpm = details.bpm,
            bpmConfidence = details.bpmConfidence,
            timeSignature = details.timeSignature,
            timeSignatureConfidence = details.timeSignatureConfidence,
            key = details.key,
            keyConfidence = details.keyConfidence,
            modeConfidence = details.modeConfidence,
            genres = details.genres
        )
    }

}