package ua.leonidius.beatinspector.repos

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.entities.SongSearchResult

interface SongsRepository {

    /**
     * @throws SongDataIOException
     */
    suspend fun searchForSongsByTitle(q: String):  List<SongSearchResult>

    suspend fun getTrackDetails(id: String): Song

    val resultsFlow: Flow<Resource<List<SongSearchResult>>>

    suspend fun search(q: String)

}