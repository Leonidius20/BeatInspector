package ua.leonidius.beatinspector.datasources.network

import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.datasources.network.mappers.toDomainObject
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.toUIException

class SavedTracksNetworkDataSource(
    private val service: SavedTracksService,
) {

    suspend fun get(): List<SongSearchResult> {
        when (val resp = service.getSavedTracks(50, 0)) { // TODO: pagination
            is NetworkResponse.Success -> {
                return resp.body.items.map { it.track.toDomainObject() }
            }
            is NetworkResponse.Error -> {
                throw resp.toUIException()
            }
        }
    }

}