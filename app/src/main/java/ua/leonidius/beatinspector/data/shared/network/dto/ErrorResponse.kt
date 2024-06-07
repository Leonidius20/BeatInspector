package ua.leonidius.beatinspector.data.shared.network.dto

import androidx.annotation.Keep

@Keep
data class ErrorResponse(
    val status: Int,
    val message: String,
)
