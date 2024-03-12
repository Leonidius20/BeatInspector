package ua.leonidius.beatinspector.data.tracks.lists.top

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.lists.top.network.dto.TopTracksResponse
import ua.leonidius.beatinspector.data.tracks.lists.top.network.api.TopTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource

class TopTracksPagingDataSource(
    topTracksApi: TopTracksApi,
    cache: SongTitlesInMemCache,
    hideExplicit: Flow<Boolean>,
): BaseTrackPagingDataSource<TopTracksResponse>(
    topTracksApi::getTopTracks,
    cache,
    hideExplicit,
)