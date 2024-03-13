package ua.leonidius.beatinspector.data.auth.storage

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Named

class AuthStateSharedPrefStorage @Inject constructor(
    @Named("tokens_cache") private val prefs: SharedPreferences
) {

    // todo maybe put the flow here, and make wrapper authState.update functions
    // that also update the json in shareprefs and make a flow emit updated AuthState

    private val PREF_KEY_AUTH_STATE = "auth_state"

    fun storeJson(jsonString: String) {
        with(prefs.edit()) {
            putString(PREF_KEY_AUTH_STATE, jsonString)
            apply()
        }
    }

    fun getJson() = prefs.getString(PREF_KEY_AUTH_STATE, null)!! // should throw if not found, bc you should check isAuthStateStored() first

    fun isAuthStateStored(): Boolean {
        return prefs.contains(PREF_KEY_AUTH_STATE)
    }

    fun clear() {
        with(prefs.edit()) {
            remove(PREF_KEY_AUTH_STATE)
            apply()
        }
    }

}