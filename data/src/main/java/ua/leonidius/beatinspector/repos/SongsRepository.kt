package ua.leonidius.beatinspector.repos

import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult

interface SongsRepository {

    /**
     * @throws SongDataIOException
     */
    suspend fun searchForSongsByTitle(q: String):  List<SongSearchResult>

}