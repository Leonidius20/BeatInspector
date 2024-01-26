package ua.leonidius.beatinspector.views

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.ui.theme.ChangeStatusBarColor
import ua.leonidius.beatinspector.viewmodels.SearchViewModel

typealias SongId = String

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onNavigateToSongDetails: (SongId) -> Unit = {},
    windowSize: WindowSizeClass,
) {
    ChangeStatusBarColor(colorArgb = MaterialTheme.colorScheme.primary.toArgb())

    SearchScreen(
        modifier,
        windowSize,
        query = searchViewModel.query,
        onQueryChange = { searchViewModel.query = it },
        searchResults = searchViewModel.searchResults, // todo: should i bring this down to SearchResultsList for better performance?
        onSearch = { searchViewModel.performSearch() },
        onNavigateToSongDetails = onNavigateToSongDetails,
        state = searchViewModel.uiState,
        errorMessage = searchViewModel.errorMessageId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit = {},
    state: SearchViewModel.UiState = SearchViewModel.UiState.LOADED,
    @StringRes errorMessage: Int? = null
) {
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    //Scaffold(
   //     snackbarHost = { SnackbarHost(snackbarHostState) },
   // ) { paddingValues ->
        SearchBar(
            // modifier = Modifier.requiredHeight(100.dp),
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
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.clickable(onClickLabel = "clear search query") { // todo: add localization
                        if (query.isNotEmpty()) {
                            onQueryChange("")
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            },
        ) {
            when(state) {
                SearchViewModel.UiState.UNINITIALIZED -> {
                    // nothing
                }
                SearchViewModel.UiState.LOADING -> {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.Center)
                        )
                    }

                }
                SearchViewModel.UiState.LOADED -> {
                    when(windowSize.widthSizeClass) {
                        WindowWidthSizeClass.Compact -> {
                            SearchResultsList(
                                results = searchResults,
                                onNavigateToSongDetails = onNavigateToSongDetails
                            )
                        }
                        WindowWidthSizeClass.Expanded-> {
                            SearchResultsGrid(
                                results = searchResults,
                                onNavigateToSongDetails = onNavigateToSongDetails
                            )
                        }
                    }
                    /*SearchResultsList(
                        //Modifier.height(100.dp), // todo
                        results = searchResults,
                        onNavigateToSongDetails = onNavigateToSongDetails
                    )*/
                }
                SearchViewModel.UiState.ERROR -> {
                    val message = stringResource(id = errorMessage!!)
                    val context = LocalContext.current
                    LaunchedEffect(key1 = errorMessage) {
                        ///snackbarHostState.showSnackbar(
                        //    message = message,
                        //    duration = SnackbarDuration.Short
                       // )
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } // todo: is this  the right way to start a snackbar?
                    Text(text = message) // todo: snackbar
                }
            }


        }
   // }


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
    val song1 = SongSearchResult("1", "song1", listOf(artist1, artist2), "")
    val song2 = SongSearchResult("2", "song2", listOf(artist1), "")

    SearchResultsGrid(
        results = listOf(song1, song2),
        onNavigateToSongDetails = {}
    )
}

/*@Composable
fun SearchResult(
    modifier: Modifier = Modifier,
    title: String,
    artist: String
) {
    Surface(
        modifier
            .shadow(elevation = 5.dp)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = artist)
        }
    }

}*/

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