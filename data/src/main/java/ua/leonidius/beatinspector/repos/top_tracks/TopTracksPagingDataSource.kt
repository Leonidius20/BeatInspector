package ua.leonidius.beatinspector.repos.top_tracks

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.datasources.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.datasources.network.dto.responses.TopTracksResponse
import ua.leonidius.beatinspector.datasources.network.services.TopTracksApi
import ua.leonidius.beatinspector.repos.BaseTrackPagingDataSource

class TopTracksPagingDataSource(
    topTracksApi: TopTracksApi,
    cache: SongTitlesInMemCache,
    hideExplicit: Flow<Boolean>,
): BaseTrackPagingDataSource<TopTracksResponse>(
    topTracksApi::getTopTracks,
    cache,
    hideExplicit,
)