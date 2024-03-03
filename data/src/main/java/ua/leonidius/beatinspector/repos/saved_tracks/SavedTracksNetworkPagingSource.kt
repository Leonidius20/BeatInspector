package ua.leonidius.beatinspector.repos.saved_tracks

import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.dto.SavedTracksResponse
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService
import ua.leonidius.beatinspector.repos.BaseTrackPagingDataSource

class SavedTracksNetworkPagingSource(
    service: SavedTracksService,
    searchCache: SearchCacheDataSource,
    hideExplicit: () -> Boolean,
): BaseTrackPagingDataSource<SavedTracksResponse>(
    service::getSavedTracks,
    searchCache,
    hideExplicit,
)