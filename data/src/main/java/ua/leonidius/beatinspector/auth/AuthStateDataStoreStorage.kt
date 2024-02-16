package ua.leonidius.beatinspector.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthStateDataStoreStorage(
    private val dataStore: DataStore<Preferences>
): AuthStateFlowingStorage {

    private val authStatePrefKey = stringPreferencesKey("auth_state")

    override val jsonAuthStateFlow: Flow<String?> = dataStore.data.map { preferences  ->
        preferences[authStatePrefKey] // can be null
    }

    override suspend fun storeJson(jsonString: String) {
        dataStore.edit { preferences ->
            preferences[authStatePrefKey] = jsonString
        }
    }

}