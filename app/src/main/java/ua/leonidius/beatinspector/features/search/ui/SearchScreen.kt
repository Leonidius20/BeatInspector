package ua.leonidius.beatinspector.features.search.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.data.tracks.shared.domain.Artist
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.ui.theme.ChangeStatusBarColor
import ua.leonidius.beatinspector.shared.viewmodels.PfpState
import ua.leonidius.beatinspector.features.search.viewmodels.SearchViewModel
import ua.leonidius.beatinspector.features.shared.ui.LoadingScreen
import ua.leonidius.beatinspector.features.shared.ui.SearchBoxScreenWithAttribution

typealias SongId = String

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onNavigateToSongDetails: (SongId) -> Unit = {},
    onNavigateToSettings: () -> Unit,
    onOpenSongInSpotify: (SongId) -> Unit,
    onOpenSavedTracks: () -> Unit,
) {
    ChangeStatusBarColor(colorArgb = MaterialTheme.colorScheme.primary.toArgb())

    SearchScreen(
        modifier,
        query = searchViewModel.query,
        onQueryChange = { searchViewModel.query = it },

        onSearch = { searchViewModel.performSearch() },
        onNavigateToSongDetails = onNavigateToSongDetails,
        onNavigateToSettings = onNavigateToSettings,
        state = searchViewModel.uiState,
       // playlistsPaging = searchViewModel.playlistsPagingFlow.collectAsLazyPagingItems(),
        accountImageState = searchViewModel.pfpState,
        onOpenSongInSpotify = onOpenSongInSpotify,
        onOpenSavedTracks = onOpenSavedTracks,
        // called when pressing clear function to go back to the list of user's playlists
       // onReturnToPlaylistsList = {
        //    searchViewModel.returnToUninitialized()
       // },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,

    onNavigateToSongDetails: (SongId) -> Unit = {},
    onNavigateToSettings: () -> Unit,
    state: SearchViewModel.UiState,
   // playlistsPaging: LazyPagingItems<PlaylistSearchResult>,
    accountImageState: PfpState,
    onOpenSongInSpotify: (SongId) -> Unit,
    onOpenSavedTracks: () -> Unit,
    //onReturnToPlaylistsList: () -> Unit,
) {
    var searchBarActive by rememberSaveable { mutableStateOf(false) }

    SearchBoxScreenWithAttribution(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        //onReturnToPlaylistsList = onReturnToPlaylistsList,
        onNavigateToSettings = onNavigateToSettings,
        accountImageState = accountImageState,
        searchBarActive = searchBarActive,
        setSearchBarActive = { searchBarActive = it },
    ) {
        when(state) {
            is SearchViewModel.UiState.Uninitialized -> {
                // todo: refactor maybe into a different file
                /*PlaylistsList(
                    playlists = playlistsPaging,
                    onOpenSavedTracks = onOpenSavedTracks,
                )*/
            }
            is SearchViewModel.UiState.Loading -> LoadingScreen()

            is SearchViewModel.UiState.Loaded -> {
                when(LocalConfiguration.current.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        SearchResultsGrid(
                            results = state.searchResults,
                            onNavigateToSongDetails = onNavigateToSongDetails,
                            onOpenSongInSpotify = onOpenSongInSpotify,
                        )
                    }
                    else -> {
                        SearchResultsList(
                            results = state.searchResults,
                            onNavigateToSongDetails = onNavigateToSongDetails,
                            onOpenSongInSpotify = onOpenSongInSpotify,
                        )
                    }
                }
            }
            is SearchViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(id = state.errorMessageId))
                        Card(Modifier.fillMaxWidth()) {
                            Text(text = state.errorAdditionalInfo)
                        }
                    }

                }
            }
        }
    }

}


@Composable
@Preview("SearchScreenPreview", widthDp = 320, showBackground = true)
fun SearchScreenPortraitPreview() {
    val artist1 = Artist("1", "artist1")
    val artist2 = Artist("2", "artist2")
    val song1 = SongSearchResult("1", "song1", listOf(artist1, artist2), true,"")
    val song2 = SongSearchResult("2", "song2", listOf(artist1), true,"")

    SearchResultsList(
        results = listOf(song1, song2),
        onNavigateToSongDetails = {},
        onOpenSongInSpotify = {},
    )
}

@Composable
@Preview("SearchResultsGridPreview", widthDp = 720, showBackground = true)
fun SearchResultsGridPreview() {
    val artist1 = Artist("1", "artist1")
    val artist2 = Artist("2", "artist2")
    // todo: maybe there should be a factory so that we don't depend on SongSearchResult directly
    val song1 = SongSearchResult("1", "song1", listOf(artist1, artist2), true,"")
    val song2 = SongSearchResult("2", "song2", listOf(artist1), true,"")

    SearchResultsGrid(
        results = listOf(song1, song2),
        onNavigateToSongDetails = {},
        onOpenSongInSpotify = {},
    )
}

@Composable
fun SearchResultsList(
    modifier: Modifier = Modifier,
    results: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit,
    onOpenSongInSpotify: (SongId) -> Unit,
) {
    LazyColumn(modifier) {
        items(results, key = { it.id }) {
            SearchResultsListItem(
                Modifier.clickable { onNavigateToSongDetails(it.id) },
                title = it.name,
                artist = it.artists.joinToString(", ") { it.name },
                onOpenSongInSpotify = { onOpenSongInSpotify(it.id) },
            )
        }
    }
}

@Composable
fun SearchResultsListItem(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    onOpenSongInSpotify: () -> Unit,
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = artist) },
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
        }
    )

}

@Composable
fun SearchResultsGrid(
    modifier: Modifier = Modifier,
    results: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit,
    onOpenSongInSpotify: (SongId) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2)
    ) {
        items(results, key = { it.id }) {
            SearchResultsListItem(
                Modifier.clickable { onNavigateToSongDetails(it.id) },
                title = it.name,
                artist = it.artists.joinToString(", ") { it.name },
                onOpenSongInSpotify = { onOpenSongInSpotify(it.id) },
            )
        }
    }
}

