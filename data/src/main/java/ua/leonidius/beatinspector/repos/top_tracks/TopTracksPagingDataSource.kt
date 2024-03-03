package ua.leonidius.beatinspector.repos.top_tracks

import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.dto.responses.TopTracksResponse
import ua.leonidius.beatinspector.datasources.network.services.TopTracksApi
import ua.leonidius.beatinspector.repos.BaseTrackPagingDataSource

class TopTracksPagingDataSource(
    topTracksApi: TopTracksApi,
    cache: SearchCacheDataSource,
    hideExplicit: () -> Boolean,
): BaseTrackPagingDataSource<TopTracksResponse>(
    topTracksApi::getTopTracks,
    cache,
    hideExplicit,
)