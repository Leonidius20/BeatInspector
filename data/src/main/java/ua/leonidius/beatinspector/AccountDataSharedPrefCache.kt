package ua.leonidius.beatinspector

import android.content.SharedPreferences
import ua.leonidius.beatinspector.entities.AccountDetails

class AccountDataSharedPrefCache(
    private val prefs: SharedPreferences
): AccountDataCache {

    private val prefUsernameKey = "username"
    private val prefIdKey = "id"
    private val prefImageUrl = "imageUrl"

    // todo: it may be a good idea have the data as livedata or stateflow, so that any viewmodel can observe it and update ui accordingly

    override fun retrieve(): AccountDetails {
        TODO("Not yet implemented")
    }

    override fun store(details: AccountDetails) {
        TODO("Not yet implemented")
    }

    override fun isDataAvailable(): Boolean {
        return false
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}