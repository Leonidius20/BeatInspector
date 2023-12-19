package ua.leonidius.beatinspector.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import ua.leonidius.beatinspector.viewmodels.SearchViewModel
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.entities.SongSearchResult

typealias SongId = String

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onNavigateToSongDetails: (SongId) -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    // todo: there's some better way, perhaps with better performance
    // https://medium.com/androiddevelopers/effective-state-management-for-textfield-in-compose-d6e5b070fbe5

    SearchScreen(
        modifier,
        query = searchViewModel.query,
        onQueryChange = { searchViewModel.query = it },
        searchResults = searchViewModel.searchResults, // todo: should i bring this down to SearchResultsList for better performance?
        onSearch = { searchViewModel.performSearch() },
        onNavigateToSongDetails = onNavigateToSongDetails
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
    onNavigateToSongDetails: (SongId) -> Unit = {}
) {
    SearchBar(
        // modifier = Modifier.requiredHeight(100.dp),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        placeholder = { Text(stringResource(R.string.searchBar_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        },
        active = true,
        onActiveChange = {}
    ) {
        SearchResultsList(
            //Modifier.height(100.dp), // todo
            results = searchResults,
            onNavigateToSongDetails = onNavigateToSongDetails
        )
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

@Composable
fun SearchResult(
    modifier: Modifier = Modifier,
    title: String,
    artist: String
) {
    Surface(
        modifier.shadow(elevation = 5.dp).fillMaxWidth().padding(5.dp)
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = artist)
        }
    }

}

@Composable
fun SearchResultsList(
    modifier: Modifier = Modifier,
    results: List<SongSearchResult>,
    onNavigateToSongDetails: (SongId) -> Unit
) {
    LazyColumn(modifier.padding(5.dp)) {
        items(results) {
            SearchResult(
                Modifier.clickable { onNavigateToSongDetails(it.id) },
                title = it.name,
                artist = it.artist
            )
        }
    }
}