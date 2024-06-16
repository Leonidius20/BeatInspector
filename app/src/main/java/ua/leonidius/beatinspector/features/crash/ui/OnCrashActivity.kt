package ua.leonidius.beatinspector.features.crash.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.ui.theme.BeatInspectorTheme
import ua.leonidius.beatinspector.ui.theme.Dimens

/**
 * this activity is launched if the app crashes. It allows to copy the stacktrace
 * and contains a link to github issues. Also allows to restart the app.
 * TODO: i suppose it would be a good idea to avoid initializing anything when the
 * app launches in DI module, bc there can be a crash there and i am not sure if
 * uncaught exception handler will handle that, or if it won't crush again when
 * trying to show this activity. (we can test this)
 * Scope everything to viewmodels, if something has
 * to be initialized in app module, do it lazily.
 */
class OnCrashActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = CustomActivityOnCrash.getConfigFromIntent(intent)

        if (config == null) {
            //This should never happen - Just finish the activity to avoid a recursive crash.
            finish()
            return
        }

        setContent {
            BeatInspectorTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    var isErrorDataCopied by rememberSaveable { mutableStateOf(false) }

                    CrashScreen(isErrorDataCopied = isErrorDataCopied, buttonCallbacks = ButtonCallbacks(
                        copyCrashData = {
                            val errorData = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, intent)
                            copyToClipboard(errorData)
                            isErrorDataCopied = true
                        },
                        goToGitHubIssues = {
                            val url = "https://github.com/Leonidius20/BeatInspector/issues"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)

                            CustomActivityOnCrash.closeApplication(this, config)
                        },
                        restartApp = {
                            CustomActivityOnCrash.restartApplication(this, config)
                        },
                        dismiss = {
                            CustomActivityOnCrash.closeApplication(this, config)
                        }
                    ))
                }
            }
        }

        setFinishOnTouchOutside(false)
    }

    fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("BeatInspector crash data", text)
        clipboard.setPrimaryClip(clip)
    }


}

data class ButtonCallbacks(
    val copyCrashData: () -> Unit,
    val goToGitHubIssues: () -> Unit,
    val restartApp: () -> Unit,
    val dismiss: () -> Unit
)

@Composable
fun CrashScreen(
    buttonCallbacks: ButtonCallbacks,
    isErrorDataCopied: Boolean,
) {
    
    Column(
        modifier = Modifier
            .padding(Dimens.paddingLarge)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.padding(Dimens.paddingNormal))

        Text(
            text = "The app has crashed. If you want to report this crash, please copy the crash data to the clipboard, create an issue on GitHub explaining the circumstances of the crash, and paste the crash data into the description.",
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Justify),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.padding(Dimens.paddingLarge))

        if (!isErrorDataCopied) {
            LargeButtonWithIcon(onClick = buttonCallbacks.copyCrashData,
                icon = Icons.Filled.ContentCopy, text = "Copy crash data")
        } else {
            LargeButtonWithIcon(onClick = { }, enabled = false,
                icon = Icons.Filled.Done, text = "Crash data copied")
        }

        Spacer(modifier = Modifier.padding(Dimens.paddingSmall))

        LargeButtonWithIcon(onClick = buttonCallbacks.goToGitHubIssues,
            icon = Icons.AutoMirrored.Filled.OpenInNew,
            text = "Go to GitHub issues")

        Spacer(modifier = Modifier.padding(Dimens.paddingSmall))

        LargeButtonWithIcon(onClick = buttonCallbacks.restartApp,
            icon = Icons.Filled.RestartAlt,
            text = "Restart the app")

        Spacer(modifier = Modifier.padding(Dimens.paddingSmall))

        LargeButtonWithIcon(onClick = buttonCallbacks.dismiss,
            icon = Icons.Filled.Close,
            text = "Dismiss")
    }
}

@Composable
fun LargeButtonWithIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        enabled = enabled,
        onClick = onClick) {
        Row {
            Icon(imageVector = icon,
                contentDescription = null)
            Spacer(modifier = Modifier.padding(Dimens.paddingSmall))
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = text
            )
        }
    }
}

@Preview
@Composable
fun CrashScreenPreview() {
    BeatInspectorTheme {
        CrashScreen(
            ButtonCallbacks(
                copyCrashData = {},
                goToGitHubIssues = {},
                restartApp = {},
                dismiss = {}
            ),
            isErrorDataCopied = false
        )
    }
}