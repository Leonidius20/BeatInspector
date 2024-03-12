package ua.leonidius.beatinspector.data.auth.storage

interface AuthStateStorage {

    fun getJson(): String

    fun storeJson(jsonString: String)

    fun isAuthStateStored(): Boolean

    fun clear()

}