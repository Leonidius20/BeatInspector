package ua.leonidius.beatinspector.repos.saved_tracks

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService

class SavedTracksRepositoryImpl(
    private val service: SavedTracksService,
) {

    fun get(): Flow<List<SongSearchResult>> {

        val networkFlow = flow {
            when (val resp = service.getSavedTracks(50, 0)) { // TODO: pagination
                is NetworkResponse.Success -> {
                    val tracks = resp.body.items.map { it.track }/*.map { track ->
                        SongSearchResult(
                            id = track.id,
                            name = track.name,
                            artists = track.artists.map { it.name }
                        )
                    }*/
                    emit(tracks)
                }
                is NetworkResponse.Error -> {
                    // throw resp.error
                    // todo: how to emit error?
                }
            }

        }

        //val cacheFlow = flow {
            // emit from cache
        //}

        val flow = networkFlow // .merge(cacheFlow)

        TODO("nit impl")
    }


}