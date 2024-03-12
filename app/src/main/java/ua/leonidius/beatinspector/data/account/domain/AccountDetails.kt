package ua.leonidius.beatinspector.data.account.domain

data class AccountDetails(
    val id: String,
    val username: String,
    val smallImageUrl: String?,
    val bigImageUrl: String?,
)