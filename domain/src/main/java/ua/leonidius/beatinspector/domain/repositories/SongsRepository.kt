package ua.leonidius.beatinspector.domain.repositories

import ua.leonidius.beatinspector.domain.entities.SongDetails
import ua.leonidius.beatinspector.domain.entities.SongSearchResult

interface SongsRepository {

    suspend fun searchForSongsByTitle(q: String):  List<SongSearchResult>

    suspend fun getTrackDetails(id: String): SongDetails

}