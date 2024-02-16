package ua.leonidius.beatinspector.auth

import kotlinx.coroutines.flow.Flow

interface AuthStateFlowingStorage {

    val jsonAuthStateFlow: Flow<String?>

    suspend fun storeJson(jsonString: String)

}