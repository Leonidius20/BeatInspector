package ua.leonidius.beatinspector.data.tracks.lists.liked

import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto.LikedTracksResponse
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache

class SavedTracksNetworkPagingSource(
    service: LikedTracksApi,
    searchCache: SongTitlesInMemCache,
    hideExplicit: Flow<Boolean>,
): BaseTrackPagingDataSource<LikedTracksResponse>(
    service::getSavedTracks,
    searchCache,
    hideExplicit,
)