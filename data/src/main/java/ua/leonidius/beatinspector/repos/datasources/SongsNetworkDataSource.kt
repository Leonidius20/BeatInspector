package ua.leonidius.beatinspector.repos.datasources

import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongDetails

interface SongsNetworkDataSource {

    suspend fun getTrackAudioAnalysis(trackId: String, artists: List<Artist>): SongDetails

}