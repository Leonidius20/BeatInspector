package ua.leonidius.beatinspector.data.tracks.lists.liked

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.datasources.network.dto.SavedTracksResponse
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache

class SavedTracksNetworkPagingSource(
    service: SavedTracksService,
    searchCache: SongTitlesInMemCache,
    hideExplicit: Flow<Boolean>,
): BaseTrackPagingDataSource<SavedTracksResponse>(
    service::getSavedTracks,
    searchCache,
    hideExplicit,
)