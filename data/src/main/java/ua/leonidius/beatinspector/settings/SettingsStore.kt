package ua.leonidius.beatinspector.settings

import android.content.SharedPreferences

class SettingsStore(
    private val prefs: SharedPreferences
) {

    // todo: cache in memory?

    var hideExplicit: Boolean
        get() = prefs.getBoolean("hide_explicit", false)
        set(value) = prefs.edit().putBoolean("hide_explicit", value).apply()

}