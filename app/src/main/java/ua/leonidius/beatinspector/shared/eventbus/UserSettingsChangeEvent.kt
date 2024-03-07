package ua.leonidius.beatinspector.shared.eventbus

/**
 * Fired when the user changes a setting, or a user's action
 * somehow should result in a settings change (e.g. if when
 * logging in the user indicated that they are under 19, an
 * event should be fired to update explicit content hiding setting.
 */
open class UserSettingsChangeEvent(

): Event

class UserHideExplicitSettingChangeEvent(
    val newValue: Boolean
): UserSettingsChangeEvent()