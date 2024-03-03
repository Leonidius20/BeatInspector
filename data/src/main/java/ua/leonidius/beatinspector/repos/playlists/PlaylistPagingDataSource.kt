package ua.leonidius.beatinspector.repos.playlists

import ua.leonidius.beatinspector.datasources.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.datasources.network.dto.responses.PlaylistResponse
import ua.leonidius.beatinspector.datasources.network.services.PlaylistApi
import ua.leonidius.beatinspector.repos.BaseTrackPagingDataSource

/**
 * Contents of a playlist
 */
class PlaylistPagingDataSource(
    api: PlaylistApi,
    searchCache: SongTitlesInMemCache,
    playlistId: String,
    hideExplicit: () -> Boolean,
): BaseTrackPagingDataSource<PlaylistResponse>(
    { limit, offset -> api.getTracks(playlistId, limit, offset) },
    searchCache,
    hideExplicit,
)