package ua.leonidius.beatinspector

import ua.leonidius.beatinspector.entities.AccountDetails

interface AccountDataCache {

    fun store(details: AccountDetails)

    fun retrieve(): AccountDetails

    fun isDataAvailable(): Boolean

    fun clear()

}