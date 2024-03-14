package ua.leonidius.beatinspector.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.UserHideExplicitSettingChangeEvent
import ua.leonidius.beatinspector.shared.domain.SettingsState
import javax.inject.Inject
import javax.inject.Named

class SettingsRepository @Inject constructor(
    @Named("general") prefs: DataStore<Preferences>,
    eventBus: EventBus,
) {

    private val hideExplicitKey = booleanPreferencesKey("hide_explicit")

    val settingsFlow = prefs.data.map {
        SettingsState(
            hideExplicit = it[hideExplicitKey] ?: false
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