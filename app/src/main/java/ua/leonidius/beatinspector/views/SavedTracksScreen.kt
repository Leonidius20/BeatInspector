package ua.leonidius.beatinspector.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.Flow
import ua.leonidius.beatinspector.Dimens
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.viewmodels.SavedTracksViewModel
import ua.leonidius.beatinspector.views.components.LoadingScreen

@Composable
fun SavedTracksScreen(
    viewModel: SavedTracksViewModel = viewModel(factory = SavedTracksViewModel.Factory),
    onOpenSongInSpotify: (String) -> Unit,
    onNavigateToSongDetails: (String) -> Unit,
) {
    SavedTracksScreen(
        //uiState = viewModel.uiState,
        pagingFlow = viewModel.flow,
        onOpenSongInSpotify = onOpenSongInSpotify,
        onNavigateToSongDetails = onNavigateToSongDetails,
    )
}

@Composable
private fun SavedTracksScreen(
    //uiState: SavedTracksViewModel.UiState,
    pagingFlow: Flow<PagingData<SongSearchResult>>,
    onOpenSongInSpotify: (String) -> Unit,
    onNavigateToSongDetails: (String) -> Unit,
) {
    // todo: landscape grid

    val lazyItems = pagingFlow.collectAsLazyPagingItems()

    LazyColumn {
        if (lazyItems.loadState.refresh == LoadState.Loading) {
            item {
                LoadingScreen()
            }
        }

        items(count = lazyItems.itemCount) { index ->
            val track = lazyItems[index] ?: return@items
            TrackListItem(
                Modifier.clickable {
                    onNavigateToSongDetails(track.id)
                },
                track = track,
                onOpenSongInSpotify = { onOpenSongInSpotify(track.id) }
            )

        }

        if (lazyItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }







    /*when(uiState) {
        is SavedTracksViewModel.UiState.Loading -> {
            LoadingScreen()
        }
        is SavedTracksViewModel.UiState.Error -> {
            // todo: universal error screen
            Column {
                Text(stringResource(id = uiState.errorMsgId))
                Text(uiState.errorAdditionalInfo)
            }

        }
        is SavedTracksViewModel.UiState.Loaded -> {
            SavedTracksScreenLoaded(
                uiState.tracks,
                onOpenSongInSpotify = onOpenSongInSpotify,
                onNavigateToSongDetails = onNavigateToSongDetails,
            )
        }
    }*/

}

/*@Composable
private fun SavedTracksScreenLoaded(
    tracks: List<SongSearchResult>,
    onOpenSongInSpotify: (String) -> Unit,
    onNavigateToSongDetails: (String) -> Unit,
) {
    LazyColumn() {
        item {
            Text(
                text = "Liked Tracks",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(Dimens.paddingNormal),
            )
        }

        items(tracks.size) {index ->
            val track = tracks[index]
            TrackListItem(
                Modifier.clickable {
                    onNavigateToSongDetails(track.id)
                },
                track = track,
                onOpenSongInSpotify = { onOpenSongInSpotify(track.id) }
            )
        }
    }
}*/

@Composable
private fun TrackListItem(
    modifier: Modifier = Modifier,
    track: SongSearchResult,
    onOpenSongInSpotify: () -> Unit,
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = track.name) },
        supportingContent = { Text(text = track.artists.joinToString(", ") { it.name }) },
        trailingContent = {
            IconButton(
                onClick = onOpenSongInSpotify,
                modifier = Modifier.offset(x = (-3.75).dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.spotify_icon_black),
                    contentDescription = "Open track on Spotify",
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .aspectRatio(1f),
                model = track.imageUrl,
                contentDescription = null
            )
        },
    )
}