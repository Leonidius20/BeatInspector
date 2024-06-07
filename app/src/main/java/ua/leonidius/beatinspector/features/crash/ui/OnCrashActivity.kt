package ua.leonidius.beatinspector.features.crash.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
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

        setContent {
            BeatInspectorTheme {
                Surface(
                    modifier = Modifier.padding(Dimens.paddingNormal),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text(text = "the app crashed. sorry")
                    }

                }
            }
        }

        setFinishOnTouchOutside(false)
    }

}