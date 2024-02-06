package ua.leonidius.beatinspector.entities

data class AccountDetails(
    val id: String,
    val username: String,
    val imageUrl: String?,

    // todo: there can be a small and a big image
)