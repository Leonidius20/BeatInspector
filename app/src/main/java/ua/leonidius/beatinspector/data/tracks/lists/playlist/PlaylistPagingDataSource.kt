package ua.leonidius.beatinspector.data.tracks.lists.playlist

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.api.PlaylistApi
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.dto.PlaylistResponse
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.shared.domain.SettingsState
import javax.inject.Inject

/**
 * Contents of a playlist
 */
class PlaylistPagingDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    api: PlaylistApi,
    searchCache: SongTitlesInMemCache,
    settingsFlow: Flow<SettingsState>,
): BaseTrackPagingDataSource<PlaylistResponse>(
    { limit, offset -> api.getTracks(savedStateHandle.get<String>("playlistId")!!, limit, offset) },
    searchCache,
    settingsFlow.map { it.hideExplicit },
)