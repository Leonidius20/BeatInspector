package ua.leonidius.beatinspector.datasources.network

import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService

class SavedTracksNetworkDataSource(
    private val service: SavedTracksService,
) {

    suspend fun get() {
        TODO()
    }

}