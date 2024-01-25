package ua.leonidius.beatinspector.repos.datasources

import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongDetails

interface SongsNetworkDataSource {

    /**
     * @return Pair<SongDetails, List<String>> where the first element is the song details and the second is the list of errored out artists (for which genres could not be loaded)
     */
    suspend fun getSongDetailsById(trackId: String, artists: List<Artist>): Pair<SongDetails, List<String>>

}