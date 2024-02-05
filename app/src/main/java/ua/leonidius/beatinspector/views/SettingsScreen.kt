package ua.leonidius.beatinspector.views

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(),
    onLegalDocClicked: (Int) -> Unit,
    onLogOutClicked: () -> Unit,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        SettingsBlock(title = R.string.settings_block_account) {
            SettingsItem(title = stringResource(R.string.log_out)) {
                onLogOutClicked()
            }
        }
        SettingsBlock(title = R.string.settings_block_title_legal_docs) {
            Column {
                SettingsItem(title = stringResource(R.string.privacy_policy_title)) {
                    onLegalDocClicked(R.string.privacy_policy)
                }
                SettingsItem(title = stringResource(R.string.terms_and_conditions_title)) {
                    onLegalDocClicked(R.string.terms_and_conditions)
                }
                ExpandableSettingsItem(title = stringResource(R.string.open_source_licenses_title)) {
                    Column {
                        ExpandedSettingsItem(title = "test") {

                        }
                    }

                }
            }
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
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier.clickable(onClick = { expanded = !expanded }),
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
    if (expanded) {
        content()
    }
}

@Composable
fun ExpandedSettingsItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SettingsItem(modifier = modifier.padding(start = 18.dp), title = title, onClick = onClick)
}