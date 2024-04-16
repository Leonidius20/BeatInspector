package ua.leonidius.beatinspector.features.login.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.features.shared.ui.CenteredScrollableTextScreen

@Composable
fun LoginInProgressScreen() {
    CenteredScrollableTextScreen {
        TextBlock(
            text = stringResource(R.string.login_in_progress_tip),
        )
        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
    }
}