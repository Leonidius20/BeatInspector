package ua.leonidius.beatinspector.repos.search

import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult

interface SearchRepository {

    /**
     * @throws SongDataIOException
     */
    suspend fun searchForSongsByTitle(q: String):  List<SongSearchResult>

}