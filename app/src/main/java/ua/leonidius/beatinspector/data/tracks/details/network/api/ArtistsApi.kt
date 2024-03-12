package ua.leonidius.beatinspector.data.tracks.details.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.tracks.details.network.dto.MultipleArtistsResponse

interface ArtistsApi {

    @GET("artists")
    suspend fun getArtists(
        @Query("ids") ids: String
    ): NetworkResponse<MultipleArtistsResponse, ErrorResponse>

}