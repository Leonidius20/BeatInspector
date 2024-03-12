package ua.leonidius.beatinspector.data.tracks.lists.playlist

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.dto.PlaylistResponse
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.api.PlaylistApi
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource

/**
 * Contents of a playlist
 */
class PlaylistPagingDataSource(
    api: PlaylistApi,
    searchCache: SongTitlesInMemCache,
    playlistId: String,
    hideExplicit: Flow<Boolean>,
): BaseTrackPagingDataSource<PlaylistResponse>(
    { limit, offset -> api.getTracks(playlistId, limit, offset) },
    searchCache,
    hideExplicit,
)