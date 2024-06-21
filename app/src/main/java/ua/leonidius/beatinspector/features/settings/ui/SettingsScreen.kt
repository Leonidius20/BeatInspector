package ua.leonidius.beatinspector.features.settings.ui

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ua.leonidius.beatinspector.BuildConfig
import ua.leonidius.beatinspector.ui.theme.Dimens
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.features.settings.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    // if DataLoading, show placeholder image and empty text (or wiped out text)
    onLegalDocClicked: (Int) -> Unit,
    onLogOutClicked: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onLicenseClicked: (String) -> Unit,
) {
    SettingsScreen(
        modifier = modifier,
        accountDetailsState = viewModel.accountDetailsState,
        libraryNameAndLicenseHash = viewModel.libraryNameAndLicenseHash,
        onLegalDocClicked = onLegalDocClicked,
        onLogOutClicked = onLogOutClicked,
        onLinkClicked = onLinkClicked,
        onLicenseClicked = onLicenseClicked,
        explicitHidden = viewModel.hideExplicit.collectAsState(initial = false).value,
        onExplicitHiddenChanged = { viewModel.toggleHideExplicit(it) },
    )
}


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    accountDetailsState: SettingsViewModel.AccountDetailsState,
    libraryNameAndLicenseHash: Array<Pair<String, String?>>,
    // if DataLoading, show placeholder image and empty text (or wiped out text)
    onLegalDocClicked: (Int) -> Unit,
    onLogOutClicked: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onLicenseClicked: (String) -> Unit,
    explicitHidden: Boolean,
    onExplicitHiddenChanged: (Boolean) -> Unit,
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    var aboutAppDialogShown by rememberSaveable { mutableStateOf(false) }
    var logoutDialogShown by rememberSaveable { mutableStateOf(false) }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (!isLandscape) {
        SettingsScreenPortrait(
            modifier = modifier,
            accountDetailsState = accountDetailsState,
            libraryNameAndLicenseHash = libraryNameAndLicenseHash,
            onLegalDocClicked = onLegalDocClicked,
            onLogOutOptionChosen = { logoutDialogShown = true },
            onAboutOptionChosen = { aboutAppDialogShown = true },
            onLinkClicked = onLinkClicked,
            onLicenseClicked = onLicenseClicked,
            expanded = expanded.value,
            onExpansionStateChanged = { expanded.value = !expanded.value },
            explicitHidden = explicitHidden,
            onExplicitHiddenChanged = onExplicitHiddenChanged,
        )
    } else {
        SettingsScreenLandscape(
            modifier = modifier,
            accountDetailsState = accountDetailsState,
            libraryNameAndLicenseHash = libraryNameAndLicenseHash,
            onLegalDocClicked = onLegalDocClicked,
            onLogOutOptionChosen = { logoutDialogShown = true },
            onAboutOptionChosen = { aboutAppDialogShown = true },
            onLinkClicked = onLinkClicked,
            onLicenseClicked = onLicenseClicked,
            expanded = expanded.value,
            onExpansionStateChanged = { expanded.value = !expanded.value },
            explicitHidden = explicitHidden,
            onExplicitHiddenChanged = onExplicitHiddenChanged,
        )
    }

    if (aboutAppDialogShown) {
        AboutDialog(
            onDismissRequest = { aboutAppDialogShown = false },
        )
    }

    if (logoutDialogShown) {
        LogOutDialog(
            onLogOut = onLogOutClicked,
            onDismissRequest = { logoutDialogShown = false }
        )
    }

}

@Composable
fun SettingsScreenLandscape(
    modifier: Modifier = Modifier,
    accountDetailsState: SettingsViewModel.AccountDetailsState,
    libraryNameAndLicenseHash: Array<Pair<String, String?>>,
    onLegalDocClicked: (Int) -> Unit,
    onLogOutOptionChosen: () -> Unit,
    onAboutOptionChosen: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onLicenseClicked: (String) -> Unit,
    expanded: Boolean,
    onExpansionStateChanged: () -> Unit,
    explicitHidden: Boolean,
    onExplicitHiddenChanged: (Boolean) -> Unit,
) {
    /*NavigationRail(
        header = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back button"
                )
            }
        }
    ) {
        // maybe a word Settings but turned to the side
    }*/
    Column(modifier) {
        PageTitle(title = R.string.settings_title)

        Row {

            AccountCardLandscape(
                modifier = Modifier.weight(0.45f),
                uiState = accountDetailsState
            )

            LazyColumn(modifier.weight(0.66f)) {
                item {
                    StaticSettingsBlock(
                        expanded = expanded,
                        onExpansionStateChanged = onExpansionStateChanged,
                        onLegalDocClicked = onLegalDocClicked,
                        onLinkClicked = onLinkClicked,
                        onLogoutOptionChosen = onLogOutOptionChosen,
                        onAboutOptionChosen = onAboutOptionChosen,
                        isExplicitHidden = explicitHidden,
                        onExplicitHiddenChanged = onExplicitHiddenChanged,
                    )
                }

                if (expanded) {
                    licensesList(
                        libraryNameAndLicenseHash = libraryNameAndLicenseHash,
                        onLicenseClicked = onLicenseClicked,
                    )
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenPortrait(
    modifier: Modifier = Modifier,
    accountDetailsState: SettingsViewModel.AccountDetailsState,
    libraryNameAndLicenseHash: Array<Pair<String, String?>>,
    onLegalDocClicked: (Int) -> Unit,
    onLogOutOptionChosen: () -> Unit,
    onAboutOptionChosen: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onLicenseClicked: (String) -> Unit,
    expanded: Boolean,
    onExpansionStateChanged: () -> Unit,
    explicitHidden: Boolean,
    onExplicitHiddenChanged: (Boolean) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(stringResource(R.string.settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back button"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.padding(top = innerPadding.calculateTopPadding()),
        ) {
            item {

                AccountCardPortrait(
                    uiState = accountDetailsState,
                )

                StaticSettingsBlock(
                    expanded = expanded,
                    onExpansionStateChanged = onExpansionStateChanged,
                    onLegalDocClicked = onLegalDocClicked,
                    onLinkClicked = onLinkClicked,
                    onLogoutOptionChosen = onLogOutOptionChosen,
                    onAboutOptionChosen = onAboutOptionChosen,
                    isExplicitHidden = explicitHidden,
                    onExplicitHiddenChanged = onExplicitHiddenChanged,
                )
            }
            if (expanded) {
                licensesList(
                    libraryNameAndLicenseHash = libraryNameAndLicenseHash,
                    onLicenseClicked = onLicenseClicked,
                )
            }

        }
    }

}

private fun LazyListScope.licensesList(
    libraryNameAndLicenseHash: Array<Pair<String, String?>>,
    onLicenseClicked: (String) -> Unit,
) {
    items(libraryNameAndLicenseHash.size) { index ->
        val (name, licenseHash) = libraryNameAndLicenseHash[index]
        ExpandedSettingsItem(title = name) {
            licenseHash?.let { onLicenseClicked(it) }
        }
    }
}

@Composable
fun PageTitle(
    @StringRes title: Int,
) {
    Text(
        text = stringResource(id = title),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(Dimens.paddingNormal),
    )
}

@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        text = {
            Text(
                stringResource(id = R.string.about_app_version,
                  BuildConfig.VERSION_NAME,
                  BuildConfig.BUILD_TYPE,
                ),
            )

        },
        onDismissRequest = onDismissRequest,
        confirmButton = { }
    )
}

@Composable
fun StaticSettingsBlock(
    expanded: Boolean,
    onExpansionStateChanged: () -> Unit,
    onLegalDocClicked: (Int) -> Unit,
    onLinkClicked: (String) -> Unit,
    onLogoutOptionChosen: () -> Unit,
    onAboutOptionChosen: () -> Unit,
    isExplicitHidden: Boolean,
    onExplicitHiddenChanged: (Boolean) -> Unit,
) {
    SettingsBlock(title = R.string.settings_block_account) {
        SettingsItem(
            title = stringResource(R.string.log_out),
            onClick = onLogoutOptionChosen,
        )
    }
    SettingsBlock(title = R.string.settings_block_content) {
        ToggleSettingsItem(
            title = "Hide explicit content",
            checked = isExplicitHidden,
            onCheckedChange = onExplicitHiddenChanged,
        )
    }
    SettingsBlock(title = R.string.settings_block_links) {
        val link = stringResource(R.string.github_link)
        ListItem(
            headlineContent = {
                Text(text = stringResource(R.string.github))
            },
            supportingContent = {
                Text(text = stringResource(R.string.github_subtext))
            },
            modifier = Modifier.clickable {
                onLinkClicked(link)
            }

        )
        /*LinkSettingsItem(
            title = stringResource(R.string.github),
            link = stringResource(id = R.string.github_link),
            onLinkClicked = onLinkClicked
        )*/
    }
    SettingsBlockWithTrailingExpandableItem(
        title = R.string.settings_block_title_legal_docs
    ) {
        Column {
            SettingsItem(
                title = stringResource(R.string.settings_block_about),
                onClick = onAboutOptionChosen,
            )
            SettingsItem(title = stringResource(R.string.privacy_policy_title)) {
                onLegalDocClicked(R.string.privacy_policy)
            }
            SettingsItem(title = stringResource(R.string.terms_and_conditions_title)) {
                onLegalDocClicked(R.string.terms_and_conditions)
            }
            ExpandableSettingsItem(
                title = stringResource(R.string.open_source_licenses_title),
                expanded = expanded,
                onExpansionStateChanged = onExpansionStateChanged
            )
        }
    }
}

@Composable
fun AccountCardPortrait(
    modifier: Modifier = Modifier,
    uiState: SettingsViewModel.AccountDetailsState,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(
                start = Dimens.paddingNormal,
                end = Dimens.paddingNormal,
                bottom = Dimens.paddingNormal,
                top = Dimens.paddingSmall,
            )
    ) {
        if (uiState is SettingsViewModel.AccountDetailsState.Error) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(Dimens.paddingNormal),
                text = "Failed to load account details.",
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            Row {

                val avatarUrl = if (uiState is SettingsViewModel.AccountDetailsState.Loaded)
                    uiState.bigImageUrl
                else null

                AccountAvatarImage(
                    modifier = Modifier.fillMaxHeight(),
                    imageUrl = avatarUrl,
                )

                /*is SettingsViewModel.AccountDetailsState.Loading -> {

                    }*/
                // todo: loading visalization with text and image placeholders

                Spacer(modifier = Modifier.width(Dimens.paddingNormal))

                AccountUsername(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = Dimens.paddingNormal),
                    uiState = uiState,
                )
            }
        }


    }
}

@Composable
fun AccountCardLandscape(
    modifier: Modifier = Modifier,
    uiState: SettingsViewModel.AccountDetailsState,
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                start = Dimens.paddingNormal,
                end = Dimens.paddingNormal,
                bottom = Dimens.paddingNormal
            )
    ) {
        Column {
            val avatarUrl = if (uiState is SettingsViewModel.AccountDetailsState.Loaded)
                uiState.bigImageUrl
            else null

            AccountAvatarImage(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
                imageUrl = avatarUrl
            )

            // Spacer(modifier = Modifier.height(Dimens.paddingNormal))

            AccountUsername(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        bottom = Dimens.paddingNormal,
                        start = Dimens.paddingNormal,
                        end = Dimens.paddingNormal
                    ),
                uiState = uiState,
            )
        }
    }
}

@Composable
fun AccountAvatarImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
) {
    if (imageUrl != null) {
        // todo: 1 image with different painters?
        // todo: placeholder if loading
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier
                .padding(16.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = rememberVectorPainter(Icons.Filled.AccountCircle),
        )
    } else {
        Image(
            modifier = modifier
                .padding(16.dp)
                .aspectRatio(1f),
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
        )
    }

}

@Composable
fun AccountUsername(
    modifier: Modifier = Modifier,
    uiState: SettingsViewModel.AccountDetailsState,
) {
    Column(
        modifier
    ) {
        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            Text(stringResource(R.string.logged_in_as))
        }

        val usernameText = when(uiState) {
            is SettingsViewModel.AccountDetailsState.Loaded -> {
                uiState.username
            }
            is SettingsViewModel.AccountDetailsState.Loading -> {
                "< loading data >"
            }
            is SettingsViewModel.AccountDetailsState.Error -> {
                "" // this composable would not be displayed at all
            }
        }

        Text(
            usernameText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp, top = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            // todo: text overflow ellipsis
            // todo: placeholder if loading
        )

        // todo remove this from here
        if (!isLandscape) {
            Text(stringResource(id = R.string.settings_block_account), style = MaterialTheme.typography.labelMedium)
        }

    }
}



@Composable
fun SettingsBlock(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    SettingsBlockWithTrailingExpandableItem(
        modifier = modifier.padding(bottom = Dimens.paddingNormal),
        title = title,
        content = content
    )
}


@Composable
fun SettingsBlockWithTrailingExpandableItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            text = stringResource(id = title),
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        content()
    }
}

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(text = title)
        }
    )
}

@Composable
fun ToggleSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(text = title)
        },
        trailingContent = {
            Switch(checked, onCheckedChange)
        }
    )
}

@Composable
fun ExpandableSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    expanded: Boolean,
    onExpansionStateChanged: () -> Unit,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onExpansionStateChanged),
        headlineContent = {
            Text(text = title)
        },
        trailingContent = {
            Icon(
                imageVector = if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = null
            )
        }
    )
}

@Composable
fun ExpandedSettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SettingsItem(modifier = modifier.padding(start = 18.dp), title = title, onClick = onClick)
}

@Composable
fun LinkSettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    link: String,
    onLinkClicked: (String) -> Unit,
) {
    SettingsItem(modifier = modifier, title = title, onClick = { onLinkClicked(link) })
}

@Composable
fun LogOutDialog(
    onLogOut: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.log_out))
                },
        text = {
            Text(
                text = stringResource(R.string.log_out_explanation),
                textAlign = TextAlign.Justify,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onLogOut) {
                Text(text = stringResource(R.string.log_out))
            }
        }
    )
}

@Composable
@Preview
fun SettingsScreenPreview() {
    SettingsScreen(
        accountDetailsState = SettingsViewModel.AccountDetailsState.Loaded(
            username = "username",
            bigImageUrl = null, // todo: placeholder when couldn't load
        ),
        libraryNameAndLicenseHash = arrayOf(),
        onLegalDocClicked = {},
        onLogOutClicked = {},
        onLinkClicked = {},
        onLicenseClicked = {},
        explicitHidden = false,
        onExplicitHiddenChanged = {},
    )
}