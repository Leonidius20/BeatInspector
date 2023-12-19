package ua.leonidius.beatinspector.repos

import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.entities.SongSearchResult

interface SongsRepository {

    suspend fun searchForSongsByTitle(q: String):  List<SongSearchResult>

    suspend fun getTrackDetails(id: String): SongDetails

}