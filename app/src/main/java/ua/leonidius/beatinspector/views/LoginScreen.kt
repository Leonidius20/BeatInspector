package ua.leonidius.beatinspector.views

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.AuthStatusViewModel
import ua.leonidius.beatinspector.R

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
fun LoginComposable(
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

        is AuthStatusViewModel.UiState.LoginInProgress -> LoginInProgressScreen()

        is AuthStatusViewModel.UiState.LoginError -> LoginErrorScreen(onLoginButtonPressed)

        is AuthStatusViewModel.UiState.SuccessfulLogin -> { /* this screen will not be shown */ }

    }
}

@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .fillMaxHeight()
            .padding(16.dp)
            .widthIn(min = 0.dp, max = 250.dp),
    ) {
        BoxWithConstraints(
            Modifier.align(Alignment.CenterHorizontally)
        ) {
            val boxScope = this

            val width = if (boxScope.maxWidth < 500.dp) boxScope.maxWidth else 500.dp

            Column(Modifier.width(width)) {
                content()
            }
        }
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

@Composable
fun LoginErrorScreen(
    onLoginButtonPressed: () -> Unit,
) {
    ScreenColumn {
        // todo: add actual error description based on the error type (network, user cancelled, etc)
        TextBlock(
            text = stringResource(R.string.log_in_error),
        )
        CenteredButton(
            onClick = onLoginButtonPressed
        ) {
            Text(text = stringResource(R.string.login_screen_try_again_button))
        }
    }
}

@Composable
fun LoginInProgressScreen() {
    ScreenColumn {
        TextBlock(
            text = stringResource(R.string.login_in_progress_tip),
        )
        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun LoginOfferScreen(
    onLoginButtonPressed: () -> Unit,
    onNavigateToLegalText: (Int) -> Unit,
    isUserAMinor: Boolean,
    onUserAMinorCheckboxChange: (Boolean) -> Unit,
) {
    ScreenColumn {

        val loginScopesTip = stringResource(id = R.string.login_scopes_tip)

        val annotatedString = remember {
            val linkSpanStyle = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)

            buildAnnotatedString {
                append("In order to use the app, you need to log in with your Spotify account. Press \"Log in\" and follow the instructions in the browser window. By continuing, you agree to the app's ")
                withStyle(style = linkSpanStyle) {
                    pushStringAnnotation("link", "privacy_policy")
                    append("privacy policy")
                    pop()
                }

                append(" and ")

                withStyle(style = linkSpanStyle) {
                    pushStringAnnotation("link", "terms_of_service")
                    append("terms of service")
                    pop()
                }

                append(".\n\n")

                append(loginScopesTip)
            }
        }

        ClickableText(
            annotatedString,
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Justify),
            onClick = { offset ->
                annotatedString.getStringAnnotations("link", offset, offset)
                    .firstOrNull()?.let { annotation ->
                        onNavigateToLegalText(
                            when (annotation.item) {
                                // todo: this logic should be moved to the viewmodel or elsewhere
                                "privacy_policy" -> R.string.privacy_policy
                                "terms_of_service" -> R.string.terms_and_conditions
                                else -> throw IllegalArgumentException("Unknown link annotation")
                            }
                        )
                    }
            },
        )

        LabelledCheckbox(
            modifier = Modifier
                .padding(bottom = 16.dp),
            label = stringResource(R.string.login_i_am_a_minor),
            checked = isUserAMinor,
            onCheckedChange = onUserAMinorCheckboxChange,
        )

        CenteredButton(
            onClick = onLoginButtonPressed
        ) {
            Text(text = "Log in")
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelledCheckbox(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        //}

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
        )
    }
}