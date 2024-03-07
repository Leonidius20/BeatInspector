package ua.leonidius.beatinspector.repos.saved_tracks

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.datasources.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.datasources.network.dto.SavedTracksResponse
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService
import ua.leonidius.beatinspector.repos.BaseTrackPagingDataSource

class SavedTracksNetworkPagingSource(
    service: SavedTracksService,
    searchCache: SongTitlesInMemCache,
    hideExplicit: Flow<Boolean>,
): BaseTrackPagingDataSource<SavedTracksResponse>(
    service::getSavedTracks,
    searchCache,
    hideExplicit,
)