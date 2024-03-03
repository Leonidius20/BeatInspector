package ua.leonidius.beatinspector.repos.account

import ua.leonidius.beatinspector.datasources.cache.InMemCache
import ua.leonidius.beatinspector.entities.AccountDetails

interface AccountDataCache: InMemCache<Unit, AccountDetails> {

    fun clear()

}