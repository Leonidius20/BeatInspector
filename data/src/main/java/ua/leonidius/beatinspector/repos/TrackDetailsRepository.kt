package ua.leonidius.beatinspector.repos

import ua.leonidius.beatinspector.entities.Song

interface TrackDetailsRepository {

    suspend fun getFullDetails(id: String): Song

}