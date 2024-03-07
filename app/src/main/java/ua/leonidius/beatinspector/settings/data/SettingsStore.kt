package ua.leonidius.beatinspector.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
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

    init {
        eventCollectScope.launch {
            eventBus.
                filterIsInstance<UserHideExplicitSettingChangeEvent>()
                .collectLatest { event ->
                    prefs.edit {
                        it[hideExplicitKey] = event.newValue
                    }
            }
        }

    }

}