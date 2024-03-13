package ua.leonidius.beatinspector.features.login.ui

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.features.login.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onLoginButtonPressed: () -> Unit,
    onNavigateToLegalText: (Int) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
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
    uiState: LoginViewModel.UiState,
    isUserAMinor: Boolean,
    onUserAMinorCheckboxChange: (Boolean) -> Unit,
) {
    when(uiState) {
        is LoginViewModel.UiState.LoginOffered -> LoginOfferScreen(
            onLoginButtonPressed = onLoginButtonPressed,
            onNavigateToLegalText = onNavigateToLegalText,
            isUserAMinor = isUserAMinor,
            onUserAMinorCheckboxChange = onUserAMinorCheckboxChange,
        )

        is LoginViewModel.UiState.LoginInProgress ->
            LoginInProgressScreen()

        is LoginViewModel.UiState.LoginError ->
            LoginErrorScreen(onLoginButtonPressed)

        is LoginViewModel.UiState.SuccessfulLogin ->
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


