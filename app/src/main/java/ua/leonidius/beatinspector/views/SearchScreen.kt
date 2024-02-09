package ua.leonidius.beatinspector.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ua.leonidius.beatinspector.AuthStatusViewModel
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.ui.theme.ChangeStatusBarColor
import ua.leonidius.beatinspector.viewmodels.SearchViewModel
import ua.leonidius.beatinspector.views.components.LoadingScreen

typealias SongId = String

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onNavigateToSongDetails: (SongId) -> Unit = {},
    onNavigateToSettings: () -> Unit,
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
        accountImageState = searchViewModel.accountImageState,
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
    accountImageState: SearchViewModel.AccountImageState,
) {
    val focusManager = LocalFocusManager.current

    Column {
        // search bar and attribution box
        SearchBar(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                focusManager.clearFocus()
                onSearch(it)
            },
            placeholder = { Text(stringResource(R.string.searchBar_placeholder)) },
            active = true,
            onActiveChange = { },
            leadingIcon = {
                IconButton(onClick = onNavigateToSettings) {


                    when (accountImageState) {
                        // todo: the alternative is having one Image with painters changed
                        // based on whther there is a URL or not

                        is SearchViewModel.AccountImageState.Loaded -> {
                            AsyncImage(
                                modifier = Modifier
                                    .clip(CircleShape),
                                model = accountImageState.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                placeholder = rememberVectorPainter(Icons.Filled.AccountCircle), // todo: is rememberVectorPainter a good idea?
                            )
                        }

                        is SearchViewModel.AccountImageState.NotLoaded -> {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                            )
                        }
                    }
                }

                /*Icon(
                    imageVector = Icons.Default.Search,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )*/

            },
            trailingIcon = {
                Row {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = {
                        if (query.isNotEmpty()) {
                            onQueryChange("")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    }

                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        SpotifyAttributionBoxLandscape()
                    }
                }

            },
        ) {

            when(state) {
                is SearchViewModel.UiState.Uninitialized -> {
                    // nothing
                }
                is SearchViewModel.UiState.Loading -> LoadingScreen()

                is SearchViewModel.UiState.Loaded -> {
                    when(LocalConfiguration.current.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> {
                            SearchResultsGrid(
                                results = state.searchResults,
                                onNavigateToSongDetails = onNavigateToSongDetails
                            )
                        }
                        else -> {
                            SearchResultsList(
                                results = state.searchResults,
                                onNavigateToSongDetails = onNavigateToSongDetails
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

        // box for spotify attribution
        if (LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            SpotifyAttributionBoxPortrait()
        }
    }


}

@Composable
fun SpotifyAttributionBoxPortrait(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {

        Row(
            Modifier
                .align(Alignment.Center),
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp)
                    .height(30.dp)
                    .wrapContentHeight(),
                text = "powered by",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )

            Image(
                painterResource(R.drawable.spotify_full_logo_black),
                contentDescription = null,
                modifier = Modifier
                    //.align(Alignment.CenterHorizontally)
                    .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp)
                    .height(30.dp)
            )
        }
    }
}

@Composable
fun SpotifyAttributionBoxLandscape(
    modifier: Modifier = Modifier
) {
    Box(modifier.height(70.dp)) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(start = 5.dp)) {
            Text(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 5.dp)
                    .width(70.dp)
                    .wrapContentWidth(),
                text = "powered by",
                textAlign = TextAlign.Center,
                fontSize = TextUnit(10f, TextUnitType.Sp)
            )

            Image(
                painterResource(R.drawable.spotify_full_logo_black),
                contentDescription = null,
                modifier = Modifier
                    //.align(Alignment.CenterHorizontally)
                    .padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 0.dp)
                    .width(70.dp)
            )
        }

    }
}

@Composable
@Preview("SearchScreenPreview", widthDp = 320, showBackground = true)
fun SearchScreenPortraitPreview() {
    val artist1 = Artist("1", "artist1")
    val artist2 = Artist("2", "artist2")
    val song1 = SongSearchResult("1", "song1", listOf(artist1, artist2), "")
    val song2 = SongSearchResult("2", "song2", listOf(artist1), "")

    SearchResultsList(
        results = listOf(song1, song2),
        onNavigateToSongDetails = {}
    )
}

@Composable
@Preview("SearchResultsGridPreview", widthDp = 720, showBackground = true)
fun SearchResultsGridPreview() {
    val artist1 = Artist("1", "artist1")
    val artist2 = Artist("2", "artist2")
    // todo: maybe there should be a factory so that we don't depend on SongSearchResult directly
    val song1 = SongSearchResult("1", "song1", listOf(artist1, artist2),"")
    val song2 = SongSearchResult("2", "song2", listOf(artist1), "")

    SearchResultsGrid(
        results = listOf(song1, song2),
        onNavigateToSongDetails = {}
    )
}

@Composable
fun SearchResultsList(
    modifier: Modifier = Modifier,
    results: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit
) {
    LazyColumn(modifier.padding(5.dp)) {
        items(results, key = { it.id }) {
            SearchResultsListItem(
                Modifier.clickable { onNavigateToSongDetails(it.id) },
                title = it.name,
                artist = it.artists.joinToString(", ") { it.name }
            )
        }
    }
}

@Composable
fun SearchResultsListItem(
    modifier: Modifier = Modifier,
    title: String,
    artist: String
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = artist) },
    )
}

@Composable
fun SearchResultsGrid(
    modifier: Modifier = Modifier,
    results: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2)
    ) {
        items(results, key = { it.id }) {
            SearchResultsListItem(
                Modifier.clickable { onNavigateToSongDetails(it.id) },
                title = it.name,
                artist = it.artists.joinToString(", ") { it.name }
            )
        }
    }
}