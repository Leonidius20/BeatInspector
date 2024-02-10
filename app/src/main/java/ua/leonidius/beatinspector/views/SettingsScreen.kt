package ua.leonidius.beatinspector.views

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ua.leonidius.beatinspector.BuildConfig
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
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
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    var aboutAppDialogShown by rememberSaveable { mutableStateOf(false) }
    var logoutDialogShown by rememberSaveable { mutableStateOf(false) }

    /*val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (!isLandscape) {
        SettingsScreenPortrait(
            modifier = modifier,
            accountDetailsState = accountDetailsState,
            libraryNameAndLicenseHash = libraryNameAndLicenseHash,
            onLegalDocClicked = onLegalDocClicked,
            onLogOutOptionChosen = onLogOutClicked,
            onAboutOptionChosen = { aboutAppDialogShown = true },
            onLinkClicked = onLinkClicked,
            onLicenseClicked = onLicenseClicked,
            expanded = expanded.value,
            onExpansionStateChanged = { expanded.value = !expanded.value },
        )
    }*/

    LazyColumn(content = {
        item {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
            AccountCardPortrait(uiState = accountDetailsState)


            StaticSettingsBlock(
                expanded = expanded.value,
                onExpansionStateChanged = { expanded.value = !expanded.value },
                onLegalDocClicked = onLegalDocClicked,
                onLinkClicked = onLinkClicked,
                onLogoutOptionChosen = { logoutDialogShown = true },
                onAboutOptionChosen = { aboutAppDialogShown = true },
            )
        }
        if (expanded.value) {
            items(libraryNameAndLicenseHash.size) { index ->
                val (name, licenseHash) = libraryNameAndLicenseHash[index]
                ExpandedSettingsItem(title = name) {
                    licenseHash?.let { onLicenseClicked(it) }
                }
            }
        }

    })
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
fun SettingsScreenPortrait(
    modifier: Modifier = Modifier,
    accountDetailsState: SettingsViewModel.AccountDetailsState,
    libraryNameAndLicenseHash: Array<Pair<String, String?>>,
    // if DataLoading, show placeholder image and empty text (or wiped out text)
    onLegalDocClicked: (Int) -> Unit,
    onLogOutOptionChosen: () -> Unit,
    onAboutOptionChosen: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onLicenseClicked: (String) -> Unit,
    expanded: Boolean,
    onExpansionStateChanged: () -> Unit,
) {
    LazyColumn(modifier) {
        item {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
            AccountCardPortrait(uiState = accountDetailsState)


            StaticSettingsBlock(
                expanded = expanded,
                onExpansionStateChanged = onExpansionStateChanged,
                onLegalDocClicked = onLegalDocClicked,
                onLinkClicked = onLinkClicked,
                onLogoutOptionChosen = onLogOutOptionChosen,
                onAboutOptionChosen = onAboutOptionChosen,
            )
        }
        if (expanded) {
            items(libraryNameAndLicenseHash.size) { index ->
                val (name, licenseHash) = libraryNameAndLicenseHash[index]
                ExpandedSettingsItem(title = name) {
                    licenseHash?.let { onLicenseClicked(it) }
                }
            }
        }

    }
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
                text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                textAlign = TextAlign.Justify,
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { }
    )
}

@Composable
fun LazyItemScope.StaticSettingsBlock(
    expanded: Boolean,
    onExpansionStateChanged: () -> Unit,
    onLegalDocClicked: (Int) -> Unit,
    onLinkClicked: (String) -> Unit,
    onLogoutOptionChosen: () -> Unit,
    onAboutOptionChosen: () -> Unit,
) {
    SettingsBlock(title = R.string.settings_block_account) {
        SettingsItem(
            title = stringResource(R.string.log_out),
            onClick = onLogoutOptionChosen,
        )
    }
    SettingsBlock(title = R.string.settings_block_links) {
        LinkSettingsItem(
            title = stringResource(R.string.github),
            link = stringResource(id = R.string.github_link),
            onLinkClicked = onLinkClicked
        )
    }
    SettingsBlock(
        modifier = Modifier.padding(bottom = 0.dp),
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
            .padding(16.dp)
    ) {
        if (uiState is SettingsViewModel.AccountDetailsState.Error) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                text = "Failed to load account details.",
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            Row {

                val avatarUrl = if (uiState is SettingsViewModel.AccountDetailsState.Loaded)
                    uiState.bigImageUrl
                else null

                AccountAvatarImage(imageUrl = avatarUrl)

                /*is SettingsViewModel.AccountDetailsState.Loading -> {

                    }*/
                // todo: loading visalization with text and image placeholders

                Spacer(modifier = Modifier.width(16.dp))

                AccountUsername(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    uiState = uiState,
                )
            }
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
                .fillMaxHeight()
                .padding(16.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = rememberVectorPainter(Icons.Filled.AccountCircle),
        )
    } else {
        Image(
            modifier = modifier
                .fillMaxHeight()
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
            modifier = Modifier.padding(bottom = 16.dp, top = 4.dp, end = 16.dp),
            maxLines = 2,
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
    Column(modifier) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            text = stringResource(id = title),
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    )
}