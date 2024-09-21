package ua.leonidius.beatinspector.features.tracklist.recent.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.features.tracklist.recent.viewmodels.RecentlyPlayedViewModel
import ua.leonidius.beatinspector.features.tracklist.shared.ui.TrackListActions
import ua.leonidius.beatinspector.features.tracklist.shared.ui.TrackListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyPlayedScreen(
    trackListActions: TrackListActions,
) {
    val viewModel = hiltViewModel<RecentlyPlayedViewModel>()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.home_menu_recently_played),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = trackListActions.back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_desc)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        TrackListScreen(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            actions = trackListActions,
            headerContent = {

            }
        )
    }
}