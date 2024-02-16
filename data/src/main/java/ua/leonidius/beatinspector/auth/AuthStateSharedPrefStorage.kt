package ua.leonidius.beatinspector.auth

import android.content.SharedPreferences

class AuthStateSharedPrefStorage(
    private val prefs: SharedPreferences
): AuthStateStorage {

    // todo maybe put the flow here, and make wrapper authState.update functions
    // that also update the json in shareprefs and make a flow emit updated AuthState

    private val PREF_KEY_AUTH_STATE = "auth_state"

    override fun storeJson(jsonString: String) {
        with(prefs.edit()) {
            putString(PREF_KEY_AUTH_STATE, jsonString)
            apply()
        }
    }

    override fun getJson() = prefs.getString(PREF_KEY_AUTH_STATE, null)!! // should throw if not found, bc you should check isAuthStateStored() first

    override fun isAuthStateStored(): Boolean {
        return prefs.contains(PREF_KEY_AUTH_STATE)
    }

    override fun clear() {
        with(prefs.edit()) {
            remove(PREF_KEY_AUTH_STATE)
            apply()
        }
    }

}