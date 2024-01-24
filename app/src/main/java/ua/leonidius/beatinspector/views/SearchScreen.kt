package ua.leonidius.beatinspector.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.ui.theme.ChangeStatusBarColor
import ua.leonidius.beatinspector.viewmodels.SearchViewModel

typealias SongId = String

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onNavigateToSongDetails: (SongId) -> Unit = {}
) {
    ChangeStatusBarColor(colorArgb = MaterialTheme.colorScheme.primary.toArgb())

    SearchScreen(
        modifier,
        query = searchViewModel.query,
        onQueryChange = { searchViewModel.query = it },
        searchResults = searchViewModel.searchResults, // todo: should i bring this down to SearchResultsList for better performance?
        onSearch = { searchViewModel.performSearch() },
        onNavigateToSongDetails = onNavigateToSongDetails,
        state = searchViewModel.uiState,
        errorMessage = searchViewModel.errorMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit = {},
    state: SearchViewModel.UiState = SearchViewModel.UiState.LOADED,
    errorMessage: String = ""
) {
    SearchBar(
        // modifier = Modifier.requiredHeight(100.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
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
                Text(text = "Loading...") // todo: loading indicator
            }
            SearchViewModel.UiState.LOADED -> {
                SearchResultsList(
                    //Modifier.height(100.dp), // todo
                    results = searchResults,
                    onNavigateToSongDetails = onNavigateToSongDetails
                )
            }
            SearchViewModel.UiState.ERROR -> {
                Text(text = errorMessage) // todo: snackbar
            }
        }


    }
}

@Composable
@Preview("SearchScreenPreview", widthDp = 320, showBackground = true)
fun SearchScreenPreview() {
    SearchScreen(
        query = "",
        onQueryChange = {},
        searchResults = emptyList(),
        onSearch = {}
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
                artist = it.artist
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