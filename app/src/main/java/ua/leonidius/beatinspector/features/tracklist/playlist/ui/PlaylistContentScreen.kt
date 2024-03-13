package ua.leonidius.beatinspector.features.tracklist.playlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.features.details.ui.OpenInSpotifyButton
import ua.leonidius.beatinspector.features.tracklist.playlist.viewmodels.PlaylistContentViewModel
import ua.leonidius.beatinspector.features.tracklist.shared.ui.TrackListActions
import ua.leonidius.beatinspector.features.tracklist.shared.ui.TrackListScreen
import ua.leonidius.beatinspector.ui.theme.Dimens

data class PlaylistContentActions(
    override val goToSongDetails: (String) -> Unit,
    override val openSongInSpotify: (String) -> Unit,
    val openPlaylistInSpotify: (String) -> Unit,
): TrackListActions

@Composable
fun PlaylistContentScreen(
    actions: PlaylistContentActions,
    isSpotifyInstalledFlow: Flow<Boolean>,
) {
    val model = hiltViewModel<PlaylistContentViewModel>()

    TrackListScreen(
        model,
        actions,
        headerContent = {
            when (val state = model.uiState) {
                is PlaylistContentViewModel.UiState.Loading -> {
                    // nothing
                }
                is PlaylistContentViewModel.UiState.Loaded -> {
                    PlaylistHeaderPortrait(
                        actions = actions,
                        isSpotifyInstalledFlow = isSpotifyInstalledFlow,
                        uiState = state,
                    )

                }
            }

        }
    )
}

@Composable
private fun PlaylistHeaderPortrait(
    actions: PlaylistContentActions,
    isSpotifyInstalledFlow: Flow<Boolean>,
    uiState: PlaylistContentViewModel.UiState.Loaded,
) {
    Column {
        // title
        // todo: extract it into a universal title component (for all track lists, incl. liked, top tracks, etc.)
        Text(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 25.dp, bottom = 10.dp),
            text = uiState.playlistName,
            style = MaterialTheme.typography.headlineLarge,
        )

        // centered open in spotify button
        Box(Modifier.fillMaxWidth()) {
            OpenInSpotifyButton(
                modifier = Modifier
                    .padding(Dimens.paddingNormal)
                    .align(Alignment.Center),
                onClick = { actions.openPlaylistInSpotify(uiState.uri) },
                isSpotifyInstalled = isSpotifyInstalledFlow.collectAsState(false).value,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) //todo  temorary until can extract color from playlist img
            )
        }
    }

}