package ua.leonidius.beatinspector.repos.account

import ua.leonidius.beatinspector.entities.AccountDetails

interface AccountDataCache {

    fun store(details: AccountDetails)

    fun retrieve(): AccountDetails

    fun isDataAvailable(): Boolean

    fun clear()

}