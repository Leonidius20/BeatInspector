package ua.leonidius.beatinspector.data.tracks.lists.liked.network

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.first
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.infrastructure.connectivity.Connectivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikedTracksNetworkDataSource @Inject constructor(
    private val api: LikedTracksApi,
    private val connectivity: Connectivity,
) {

    /**
     * @param page page number starting with 0
     * @param itemsPerPage items per page, has to be the same always
     */
    suspend fun getLikedTracks(page: Int, itemsPerPage: Int): List<SongSearchResult> {
        val limit = itemsPerPage
        val offset = page * limit

        val response = api.getSavedTracks(limit = limit, offset = offset)

        when(response) {
            is NetworkResponse.Success -> {
                return response.body.items.map { track -> track.toDomainObject() }
            }
            is NetworkResponse.UnknownError -> {
                throw SongDataIOException.Unknown(response.error)
            }
            is NetworkResponse.ServerError -> {
                throw SongDataIOException.Server(
                    response.code, response.body?.message ?: ""
                )
            }
            is NetworkResponse.NetworkError -> {
                if (!connectivity.isConnected.first()) {
                    throw SongDataIOException.NoInternetConnection
                } else {
                    throw SongDataIOException.Network(
                        response.error
                    )
                }
            }
        }
    }

}