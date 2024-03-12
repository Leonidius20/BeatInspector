package ua.leonidius.beatinspector.data.account.network.dto

import com.google.gson.annotations.SerializedName
import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.shared.Mapper
import ua.leonidius.beatinspector.data.shared.network.dto.ImageDto

data class AccountInfoResponse(
    val id: String,

    @SerializedName("display_name")
    val displayName: String,

    val images: List<ImageDto>
): Mapper<AccountDetails> {

    val smallestImage: ImageDto?
        get() = images.minByOrNull { it.height * it.width }

    val biggestImage : ImageDto?
        get() = images.maxByOrNull { it.height * it.width }

    override fun toDomainObject(): AccountDetails {
        return AccountDetails(
            id, displayName,
            smallestImage?.url, biggestImage?.url
        )
    }

}