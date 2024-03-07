package ua.leonidius.beatinspector.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.shared.eventbus.Event
import ua.leonidius.beatinspector.shared.eventbus.UserHideExplicitSettingChangeEvent

class SettingsStore(
    prefs: DataStore<Preferences>,
    eventBus: Flow<Event>,
    eventCollectScope: CoroutineScope,
) {

    private val hideExplicitKey = booleanPreferencesKey("hide_explicit")

    val hideExplicitFlow = prefs.data.map {
        it[hideExplicitKey] ?: false
    }

    /*suspend fun setHideExplicit(value: Boolean) {
        prefs.edit {
            it[hideExplicitKey] = value
        }
    }*/

    init {
        eventCollectScope.launch {
            eventBus.collectLatest { event ->
                if (event is UserHideExplicitSettingChangeEvent) {
                    prefs.edit {
                        it[hideExplicitKey] = event.newValue
                    }
                }
            }
        }

    }


    // todo: cache in memory?

    /*var hideExplicit: Boolean
        get() = prefs.getBoolean("hide_explicit", false)
        set(value) = prefs.edit().putBoolean("hide_explicit", value).apply()*/

}