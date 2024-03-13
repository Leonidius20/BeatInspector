package ua.leonidius.beatinspector.data.tracks.lists.liked

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto.LikedTracksResponse
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState
import javax.inject.Inject

class SavedTracksNetworkPagingSource @Inject constructor(
    service: LikedTracksApi,
    searchCache: SongTitlesInMemCache,
    settingsFlow: Flow<SettingsState>,
): BaseTrackPagingDataSource<LikedTracksResponse>(
    service::getSavedTracks,
    searchCache,
    settingsFlow.map { it.hideExplicit },
)