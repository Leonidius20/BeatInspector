package ua.leonidius.beatinspector.views

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            SettingsItem(title = R.string.log_out) {
                onLogOutClicked()
            }
        }
        SettingsBlock(title = R.string.settings_block_title_legal_docs) {
            Column {
                SettingsItem(title = R.string.privacy_policy_title) {
                    onLegalDocClicked(R.string.privacy_policy)
                }
                SettingsItem(title = R.string.terms_and_conditions_title) {
                    onLegalDocClicked(R.string.terms_and_conditions)
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
    @StringRes title: Int,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(text = stringResource(id = title))
        }
    )
}