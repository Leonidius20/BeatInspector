package ua.leonidius.beatinspector.data.tracks.lists.playlist

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.dto.PlaylistResponse
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.api.PlaylistApi
import ua.leonidius.beatinspector.data.tracks.lists.BaseTrackPagingDataSource
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState
import javax.inject.Inject

/**
 * Contents of a playlist
 */
class PlaylistPagingDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    api: PlaylistApi,
    searchCache: SongTitlesInMemCache,
    // playlistId: String,
    settingsFlow: Flow<SettingsState>,
): BaseTrackPagingDataSource<PlaylistResponse>(
    { limit, offset -> api.getTracks(savedStateHandle.get<String>("playlistId")!!, limit, offset) },
    searchCache,
    settingsFlow.map { it.hideExplicit },
)