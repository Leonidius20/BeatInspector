package ua.leonidius.beatinspector.features.shared.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.shared.viewmodels.PfpState

/**
 * Search box that is used in the main (playlists) page and
 * search results page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    // onReturnToPlaylistsList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    accountImageState: PfpState,
    searchBarActive: Boolean,
    setSearchBarActive: (Boolean) -> Unit,
) {


    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val paddingEnd = if (searchBarActive) 0.dp else if (isLandscape) 8.dp else 16.dp

    val paddingTop = if (searchBarActive) 0.dp else 5.dp
    val paddingBottom = if (searchBarActive) 0.dp else 5.dp

    Row(Modifier.padding(top = paddingTop, bottom = paddingBottom)) {




        val keyboardController = LocalSoftwareKeyboardController.current

        SearchBar(
            modifier = Modifier
                .weight(1f)
                .padding(end = paddingEnd),
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                if (query.isNotEmpty()) {
                    onSearch(it)
                    keyboardController?.hide()
                    setSearchBarActive(false)
                }
            },
            placeholder = { Text(stringResource(R.string.searchBar_placeholder)) },
            active = searchBarActive,
            onActiveChange = { setSearchBarActive(it) },
            leadingIcon = {
                if (!searchBarActive) {
                   /* if (state !is SearchViewModel.UiState.Uninitialized) {
                        IconButton(onClick = {
                            onReturnToPlaylistsList()
                            onQueryChange("")
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    } else {*/
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                   // }

                } else {
                    IconButton(onClick = { setSearchBarActive(false) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }

            },
            trailingIcon = {
                if (!searchBarActive) {
                    IconButton(
                        onClick = onNavigateToSettings,
                    ) {

                        when (accountImageState) {
                            // todo: the alternative is having one Image with painters changed
                            // based on whther there is a URL or not

                            is PfpState.Loaded -> {
                                AsyncImage(
                                    modifier = Modifier
                                        .clip(CircleShape),
                                    model = accountImageState.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    placeholder = rememberVectorPainter(Icons.Filled.AccountCircle), // todo: is rememberVectorPainter a good idea?
                                )
                            }

                            is PfpState.NotLoaded -> {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                } else {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = {
                            if (query.isNotEmpty()) {
                                onQueryChange("")
                            }
                            // onReturnToPlaylistsList()
                        }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                        )
                    }
                }
            },
        ) {
            // search suggestions here, which we do not have
        }

        if (isLandscape && !searchBarActive) {
            SpotifyAttributionBoxLandscape(Modifier.padding(end = 16.dp))
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
fun SearchBoxScreenWithAttribution(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    // onReturnToPlaylistsList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    accountImageState: PfpState,
    searchBarActive: Boolean,
    setSearchBarActive: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier) {
        // search bar and attribution box
        val paddingStart = if (searchBarActive) 0.dp else 16.dp

        Column(
            Modifier
                .weight(1f)
                .padding(start = paddingStart)) {// to make sure that the attribution box is at the bottom

            SearchBox(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                // onReturnToPlaylistsList = onReturnToPlaylistsList,
                onNavigateToSettings = onNavigateToSettings,
                accountImageState = accountImageState,
                searchBarActive = searchBarActive,
                setSearchBarActive = setSearchBarActive,
            )

            content()

        }

        if (!isLandscape) {
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
                    .height(40.dp)
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
                    .padding(start = 10.dp, end = 5.dp, top = 15.dp, bottom = 15.dp)
                    .height(30.dp)
            )
        }
    }
}