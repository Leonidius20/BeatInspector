package ua.leonidius.beatinspector.repos.datasources

import ua.leonidius.beatinspector.entities.SongDetails

interface SongsDataSource {

    suspend fun getSongDetailsById(trackId: String): SongDetails?

}