package ua.leonidius.beatinspector.data.tracks.lists.top

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.lists.top.network.dto.TopTracksResponse
import ua.leonidius.beatinspector.data.tracks.lists.top.network.api.TopTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState
import javax.inject.Inject

class TopTracksPagingDataSource @Inject constructor(
    topTracksApi: TopTracksApi,
    cache: SongTitlesInMemCache,
    settingsFlow: Flow<SettingsState>,
): BaseTrackPagingDataSource<TopTracksResponse>(
    topTracksApi::getTopTracks,
    cache,
    settingsFlow.map { it.hideExplicit },
)