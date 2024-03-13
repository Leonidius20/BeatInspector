package ua.leonidius.beatinspector.data.account.cache

import android.content.SharedPreferences
import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserLogoutRequestEvent
import javax.inject.Inject
import javax.inject.Named

class AccountDataCache @Inject constructor(
    @Named("account_cache") private val prefs: SharedPreferences,
    eventBus: EventBus,
): InMemCache<Unit, AccountDetails> {

    override val cache: MutableMap<Unit, AccountDetails> = mutableMapOf() // todo: remove this

    private val prefUsernameKey = "username"
    private val prefIdKey = "id"
    private val prefSmallImageUrl = "imageUrl"
    private val prefBigImageUrl = "bigImageUrl"

    init {
        eventBus.subscribe(UserLogoutRequestEvent::class) {
            clear()
        }
    }

    // todo: it may be a good idea have the data as datastore flow, so that any viewmodel can observe it and update ui accordingly

    override operator fun get(id: Unit): AccountDetails {
        val username = prefs.getString(prefUsernameKey, null)!! // should throw exception if null, should check isDataAvailable first
        val id = prefs.getString(prefIdKey, null)!!
        val smallImageUrl = prefs.getString(prefSmallImageUrl, null)
        val bigImageUrl = prefs.getString(prefBigImageUrl, null)

        return AccountDetails(id, username, smallImageUrl, bigImageUrl)
    }

    override operator fun set(id: Unit, data: AccountDetails) {
        with(prefs.edit()) {
            putString(prefUsernameKey, data.username)
            putString(prefIdKey, data.id)
            putString(prefSmallImageUrl, data.smallImageUrl)
            putString(prefBigImageUrl, data.bigImageUrl)
            apply()
        }
    }

    override fun has(id: Unit): Boolean {
        return prefs.contains(prefUsernameKey) && prefs.contains(prefIdKey)
    }

    fun clear() {
        with(prefs.edit()) {
            remove(prefUsernameKey)
            remove(prefIdKey)
            remove(prefSmallImageUrl)
            remove(prefBigImageUrl)
            apply()
        }
    }

}