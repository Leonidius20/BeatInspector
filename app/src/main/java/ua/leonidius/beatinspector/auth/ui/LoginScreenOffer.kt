package ua.leonidius.beatinspector.auth.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Checkbox
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
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.shared.ui.CenteredScrollableTextScreen

@Composable
fun LoginOfferScreen(
    onLoginButtonPressed: () -> Unit,
    onNavigateToLegalText: (Int) -> Unit,
    isUserAMinor: Boolean,
    onUserAMinorCheckboxChange: (Boolean) -> Unit,
) {
    CenteredScrollableTextScreen {

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