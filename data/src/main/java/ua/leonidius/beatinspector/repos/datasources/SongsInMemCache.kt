package ua.leonidius.beatinspector.repos.datasources

import ua.leonidius.beatinspector.entities.SongDetails
import ua.leonidius.beatinspector.entities.SongSearchResult

class SongsInMemCache {

    val songSearchResults = emptyMap<String, SongSearchResult>().toMutableMap()

    val songsDetails = emptyMap<String, SongDetails>().toMutableMap()

    fun getSongDetailsById(trackId: String) = songsDetails[trackId]

}