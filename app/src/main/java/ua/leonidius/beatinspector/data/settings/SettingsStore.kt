package ua.leonidius.beatinspector.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserHideExplicitSettingChangeEvent
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState

class SettingsStore(
    prefs: DataStore<Preferences>,
    eventBus: EventBus,
) {

    private val hideExplicitKey = booleanPreferencesKey("hide_explicit")

    private val hideExplicitFlow = prefs.data.map {
        it[hideExplicitKey] ?: false
    }

    val settingsFlow = hideExplicitFlow.map {
        SettingsState(
            hideExplicit = it
        )
    }

    init {

        eventBus.subscribe(UserHideExplicitSettingChangeEvent::class) { event ->
            prefs.edit {
                it[hideExplicitKey] = event.newValue
            }
        }

    }

}