package ua.leonidius.beatinspector

interface AuthStateStorage {

    fun getJson(): String

    fun storeJson(jsonString: String)

    fun isAuthStateStored(): Boolean

    fun clear()

}