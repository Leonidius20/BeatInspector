package ua.leonidius.beatinspector

import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig
import dagger.hilt.android.HiltAndroidApp
import ua.leonidius.beatinspector.features.crash.ui.OnCrashActivity

@HiltAndroidApp
class BeatInspectorApp: Application() {

    override fun onCreate() {
        super.onCreate()

        CaocConfig.Builder
            .create()
            .errorActivity(OnCrashActivity::class.java)
            .restartActivity(MainActivity::class.java)
            .apply()
    }

}
