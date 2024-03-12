package ua.leonidius.beatinspector.data.tracks.details.repository

import ua.leonidius.beatinspector.data.tracks.details.domain.Song

interface TrackDetailsRepository {

    suspend fun getFullDetails(id: String): Song

}