package ua.leonidius.beatinspector.repos.track_details

import ua.leonidius.beatinspector.entities.Song

interface TrackDetailsRepository {

    suspend fun getFullDetails(id: String): Song

}