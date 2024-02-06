package ua.leonidius.beatinspector.entities

data class AccountDetails(
    val id: String,
    val username: String,
    val smallImageUrl: String?,
    val bigImageUrl: String?,
)