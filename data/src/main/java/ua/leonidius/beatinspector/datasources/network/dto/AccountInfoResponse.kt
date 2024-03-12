package ua.leonidius.beatinspector.datasources.network.dto

import com.google.gson.annotations.SerializedName
import ua.leonidius.beatinspector.datasources.network.mappers.Mapper
import ua.leonidius.beatinspector.data.account.domain.AccountDetails

data class AccountInfoResponse(
    val id: String,

    @SerializedName("display_name")
    val displayName: String,

    val images: List<ImageDto>
): Mapper<ua.leonidius.beatinspector.data.account.domain.AccountDetails> {

    val smallestImage: ImageDto?
        get() = images.minByOrNull { it.height * it.width }

    val biggestImage : ImageDto?
        get() = images.maxByOrNull { it.height * it.width }

    override fun toDomainObject(): ua.leonidius.beatinspector.data.account.domain.AccountDetails {
        return ua.leonidius.beatinspector.data.account.domain.AccountDetails(
            id, displayName,
            smallestImage?.url, biggestImage?.url
        )
    }

}