package ua.leonidius.beatinspector.data.account.cache

import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.account.domain.AccountDetails

interface AccountDataCache: InMemCache<Unit, AccountDetails> {

    fun clear()

}