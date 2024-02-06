package ua.leonidius.beatinspector.services

import com.google.gson.annotations.SerializedName
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET

interface SpotifyAccountService {

    @GET("me")
    suspend fun getAccountInfo(): NetworkResponse<AccountInfoResponse, SpotifyRetrofitClient.SpotifyError>

    data class AccountInfoResponse(
        val id: String,

        @SerializedName("display_name")
        val displayName: String,

        val images: List<Image>
    ) {
       val smallestImage: Image?
           get() = images.minByOrNull { it.height * it.width }

       val biggestImage : Image?
           get() = images.maxByOrNull { it.height * it.width }

    }

    data class Image(
        val url: String,
        val height: Int,
        val width: Int,
    )

}