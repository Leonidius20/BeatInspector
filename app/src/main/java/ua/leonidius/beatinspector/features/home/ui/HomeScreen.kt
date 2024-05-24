package ua.leonidius.beatinspector.features.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import ua.leonidius.beatinspector.ui.theme.Dimens
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.exception.SongDataIOException
import ua.leonidius.beatinspector.shared.viewmodels.PfpState
import ua.leonidius.beatinspector.features.home.viewmodels.HomeScreenViewModel
import ua.leonidius.beatinspector.features.shared.model.toUiMessage
import ua.leonidius.beatinspector.features.shared.ui.LoadingScreen
import ua.leonidius.beatinspector.features.shared.ui.SearchBoxScreenWithAttribution

@Composable
fun HomeScreen(
    onSearch: (String) -> Unit,
    goToSettings: () -> Unit,
    goToSavedTracks: () -> Unit,
    goToRecentlyPlayed: () -> Unit,
    goToTopTracks: () -> Unit,
    goToPlaylist: (String) -> Unit,
    openPlaylistInApp: (String) -> Unit,
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()

    var searchBarActive by rememberSaveable { mutableStateOf(false) }

    var query by rememberSaveable { mutableStateOf("") }

    HomeScreen(
        viewModel.playlistsPagingFlow.collectAsLazyPagingItems(),
        viewModel.pfpState,
        query,
        onQueryChange = { query = it },
        onSearch = onSearch,
        goToSettings,
        goToSavedTracks,
        goToRecentlyPlayed,
        goToTopTracks,
        goToPlaylist,
        searchBarActive,
        setSearchBarActive = { searchBarActive = it },
        openPlaylistInApp,
    )
}

@Composable
fun HomeScreen(
    playlistsPaging: LazyPagingItems<PlaylistSearchResult>,
    pfpState: PfpState,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    goToSettings: () -> Unit,
    goToSavedTracks: () -> Unit,
    goToRecentlyPlayed: () -> Unit,
    goToTopTracks: () -> Unit,
    goToPlaylist: (String) -> Unit,
    searchBarActive: Boolean,
    setSearchBarActive: (Boolean) -> Unit,
    openPlaylistInApp: (String) -> Unit,
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
            goToTopTracks = goToTopTracks,
            goToPlaylist = goToPlaylist,
            openInApp = openPlaylistInApp,
        )
    }
}

@Composable
fun PlaylistsList(
    modifier: Modifier = Modifier,
    playlists: LazyPagingItems<PlaylistSearchResult>,
    onOpenSavedTracks: () -> Unit,
    goToRecentlyPlayed: () -> Unit,
    goToTopTracks: () -> Unit,
    goToPlaylist: (String) -> Unit,
    openInApp: (String) -> Unit,
) {
    LazyColumn(modifier) {

        item {
            PlaylistsListItem(
                onClick = onOpenSavedTracks,
                title = "Liked Tracks",
                leadingContent = {
                    Icon(
                        Icons.Filled.ThumbUp,
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
                        Icons.Filled.History,
                        contentDescription = null,
                    )
                }
            )
        }

        item {
            PlaylistsListItem(
                onClick = goToTopTracks,
                title = "Your Top Tracks",
                leadingContent = {
                    Icon(
                        Icons.Filled.Leaderboard,
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

        val networkLoadingState = playlists.loadState.source.refresh // apparently the state of loading from cache (when nothing is shown yet)
        val databaseLoadingState = playlists.loadState.mediator?.refresh // apparently the state of refreshing from network (when old cached data is shown)

        // val refreshState = playlists.loadState.refresh
        if (networkLoadingState == LoadState.Loading) {
            // todo error handling            lazyItems.loadState.refresh / append is LoadState.Error
            item {
                LoadingScreen()
            }
        } else if (networkLoadingState is LoadState.Error) {
            // todo: universal error screen
            val error = networkLoadingState.error

            item {
                Text(
                    if (error is SongDataIOException)
                        stringResource(error.toUiMessage())
                    else "Unknown error while loading, ${error.message}"
                )
            }

        }


        // NEW STUFF
        if (databaseLoadingState == LoadState.Loading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingNormal)
                )
            }
        } else if (databaseLoadingState is LoadState.Error) {
            val error = databaseLoadingState.error
            item {
                Text(
                    if (error is SongDataIOException)
                        stringResource(error.toUiMessage())
                    else "Unknown error while loading, ${error.message}"
                )
            }
        }
        // END OF NEW STUFF

        items(count = playlists.itemCount) { index ->
            val playlist = playlists[index] ?: return@items
            PlaylistWithImageListItem(
                title = playlist.name,
                imageUrl = playlist.smallImageUrl,
                onClick = { goToPlaylist(playlist.id) },
                onOpenInApp = { openInApp(playlist.uri) },
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
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(title)
        },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
    )
}

@Composable
fun PlaylistWithImageListItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    imageUrl: String?,
    onOpenInApp: () -> Unit,
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
        trailingContent = {
            IconButton(
                onClick = onOpenInApp,
                modifier = Modifier.offset(x = (-3.75).dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.spotify_icon_black),
                    contentDescription = "Open playlist on Spotify",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    )
}