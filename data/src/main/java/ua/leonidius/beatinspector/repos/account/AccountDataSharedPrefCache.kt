package ua.leonidius.beatinspector.repos.account

import android.content.SharedPreferences
import ua.leonidius.beatinspector.entities.AccountDetails

class AccountDataSharedPrefCache(
    private val prefs: SharedPreferences
): AccountDataCache {

    override val cache: MutableMap<Unit, AccountDetails> = mutableMapOf() // todo: remove this

    private val prefUsernameKey = "username"
    private val prefIdKey = "id"
    private val prefSmallImageUrl = "imageUrl"
    private val prefBigImageUrl = "bigImageUrl"

    // todo: it may be a good idea have the data as livedata or stateflow, so that any viewmodel can observe it and update ui accordingly

    override operator fun get(id: Unit): AccountDetails {
        val username = prefs.getString(prefUsernameKey, null)!! // should throw exception if null, should check isDataAvailable first
        val id = prefs.getString(prefIdKey, null)!!
        val smallImageUrl = prefs.getString(prefSmallImageUrl, null)
        val bigImageUrl = prefs.getString(prefBigImageUrl, null)

        return AccountDetails(id, username, smallImageUrl, bigImageUrl)
    }

    override operator fun set(id: Unit, details: AccountDetails) {
        with(prefs.edit()) {
            putString(prefUsernameKey, details.username)
            putString(prefIdKey, details.id)
            putString(prefSmallImageUrl, details.smallImageUrl)
            putString(prefBigImageUrl, details.bigImageUrl)
            apply()
        }
    }

    override fun has(id: Unit): Boolean {
        return prefs.contains(prefUsernameKey) && prefs.contains(prefIdKey)
    }

    override fun clear() {
        with(prefs.edit()) {
            remove(prefUsernameKey)
            remove(prefIdKey)
            remove(prefSmallImageUrl)
            remove(prefBigImageUrl)
            apply()
        }
    }

}