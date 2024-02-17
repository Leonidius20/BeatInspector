package ua.leonidius.beatinspector.repos.saved_tracks

import ua.leonidius.beatinspector.entities.SongSearchResult

interface SavedTracksRepository {

    suspend fun get(): List<SongSearchResult>

}