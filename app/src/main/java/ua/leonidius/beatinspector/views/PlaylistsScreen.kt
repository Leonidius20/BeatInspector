package ua.leonidius.beatinspector.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import ua.leonidius.beatinspector.Dimens
import ua.leonidius.beatinspector.entities.PlaylistSearchResult
import ua.leonidius.beatinspector.viewmodels.PfpState
import ua.leonidius.beatinspector.viewmodels.PlaylistsViewModel
import ua.leonidius.beatinspector.views.components.LoadingScreen
import ua.leonidius.beatinspector.views.components.SearchBoxScreenWithAttribution

@Composable
fun PlaylistsScreen(
    onSearch: (String) -> Unit,
    goToSettings: () -> Unit,
    goToSavedTracks: () -> Unit,
    goToRecentlyPlayed: () -> Unit,
) {
    val viewModel: PlaylistsViewModel = viewModel(factory = PlaylistsViewModel.Factory)

    var searchBarActive by rememberSaveable { mutableStateOf(false) }

    var query by rememberSaveable { mutableStateOf("") }

    PlaylistsScreen(
        viewModel.playlistsPagingFlow.collectAsLazyPagingItems(),
        viewModel.pfpState,
        query,
        onQueryChange = { query = it },
        onSearch = onSearch,
        goToSettings,
        goToSavedTracks,
        goToRecentlyPlayed,
        searchBarActive,
        setSearchBarActive = { searchBarActive = it },
    )
}

@Composable
fun PlaylistsScreen(
    playlistsPaging: LazyPagingItems<PlaylistSearchResult>,
    pfpState: PfpState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    goToSettings: () -> Unit,
    goToSavedTracks: () -> Unit,
    goToRecentlyPlayed: () -> Unit,
    searchBarActive: Boolean,
    setSearchBarActive: (Boolean) -> Unit,
) {
    SearchBoxScreenWithAttribution(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        onNavigateToSettings = goToSettings,
        accountImageState = pfpState,
        searchBarActive = searchBarActive,
        setSearchBarActive = setSearchBarActive,
    ) {
        PlaylistsList(
            playlists = playlistsPaging,
            onOpenSavedTracks = goToSavedTracks,
            goToRecentlyPlayed = goToRecentlyPlayed,
        )
    }
}

@Composable
fun PlaylistsList(
    modifier: Modifier = Modifier,
    playlists: LazyPagingItems<PlaylistSearchResult>,
    onOpenSavedTracks: () -> Unit,
    goToRecentlyPlayed: () -> Unit,
) {
    LazyColumn(modifier) {

        item {
            PlaylistsListItem(
                onClick = onOpenSavedTracks,
                title = "Liked Tracks",
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                    )
                }
            )
        }

        item {
            PlaylistsListItem(
                onClick = goToRecentlyPlayed,
                title = "Recently Played",
                leadingContent = {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                    )
                }
            )
        }

        item {
            Text(
                modifier = Modifier.padding(start = Dimens.paddingNormal, end = Dimens.paddingNormal, bottom = 8.dp),
                text = "Your playlists",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        if (playlists.loadState.refresh == LoadState.Loading) {
            // todo error handling            lazyItems.loadState.refresh / append is LoadState.Error
            item {
                LoadingScreen()
            }
        }

        items(count = playlists.itemCount) { index ->
            val playlist = playlists[index] ?: return@items
            PlaylistWithImageListItem(
                title = playlist.name,
                imageUrl = playlist.smallImageUrl,
                onClick = { /* todo: open playlist by id */},
            )

        }

        if (playlists.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingNormal)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

    }
}

@Composable
fun PlaylistsListItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    leadingContent: @Composable () -> Unit,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(title)
        },
        leadingContent = leadingContent,
    )
}

@Composable
fun PlaylistWithImageListItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    imageUrl: String?,
) {
    PlaylistsListItem(
        modifier = modifier,
        title = title,
        onClick = onClick,
        leadingContent = {
            AsyncImage(
                modifier = Modifier.size(40.dp),
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberVectorPainter(Icons.Filled.AccountCircle),
            )
        },
    )
}