package ua.leonidius.beatinspector.repos.playlists

import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.dto.responses.PlaylistResponse
import ua.leonidius.beatinspector.datasources.network.services.PlaylistApi
import ua.leonidius.beatinspector.repos.BaseTrackPagingDataSource

/**
 * Contents of a playlist
 */
class PlaylistPagingDataSource(
    private val api: PlaylistApi,
    searchCache: SearchCacheDataSource,
    private val playlistId: String,
): BaseTrackPagingDataSource<PlaylistResponse>(
    { limit, offset -> api.getTracks(playlistId, limit, offset) },
    searchCache
)