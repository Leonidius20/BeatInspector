package ua.leonidius.beatinspector.auth.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.auth.viewmodels.AuthStatusViewModel

@Composable
fun LoginScreen(
    onLoginButtonPressed: () -> Unit,
    onNavigateToLegalText: (Int) -> Unit,
    viewModel: AuthStatusViewModel = viewModel(factory = AuthStatusViewModel.Factory),
) {
    LoginComposable(
        onLoginButtonPressed = onLoginButtonPressed,
        onNavigateToLegalText = onNavigateToLegalText,
        uiState = viewModel.uiState,
        isUserAMinor = viewModel.iAmAMinorOptionSelected,
        onUserAMinorCheckboxChange = { viewModel.iAmAMinorOptionSelected = it },
    )

}

@Composable
private fun LoginComposable(
    onLoginButtonPressed: () -> Unit,
    onNavigateToLegalText: (Int) -> Unit,
    uiState: AuthStatusViewModel.UiState,
    isUserAMinor: Boolean,
    onUserAMinorCheckboxChange: (Boolean) -> Unit,
) {
    when(uiState) {
        is AuthStatusViewModel.UiState.LoginOffered -> LoginOfferScreen(
            onLoginButtonPressed = onLoginButtonPressed,
            onNavigateToLegalText = onNavigateToLegalText,
            isUserAMinor = isUserAMinor,
            onUserAMinorCheckboxChange = onUserAMinorCheckboxChange,
        )

        is AuthStatusViewModel.UiState.LoginInProgress ->
            LoginInProgressScreen()

        is AuthStatusViewModel.UiState.LoginError ->
            LoginErrorScreen(onLoginButtonPressed)

        is AuthStatusViewModel.UiState.SuccessfulLogin ->
            { /* this screen will not be shown */ }

    }
}



@Composable
fun TextBlock(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text,
        modifier = modifier.padding(bottom = 16.dp),
        textAlign = TextAlign.Justify,
    )
}

@Composable
fun ColumnScope.CenteredButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        modifier = modifier.align(Alignment.CenterHorizontally),
        onClick = onClick, content = content,
    )
}


