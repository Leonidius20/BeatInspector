package ua.leonidius.beatinspector.auth

interface AuthStateStorage {

    fun getJson(): String

    fun storeJson(jsonString: String)

    fun isAuthStateStored(): Boolean

    fun clear()

}