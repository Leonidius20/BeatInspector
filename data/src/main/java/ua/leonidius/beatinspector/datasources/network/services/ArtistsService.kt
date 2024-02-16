package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.MultipleArtistsResponse

interface ArtistsService {

    @GET("artists")
    suspend fun getArtists(
        @Query("ids") ids: String
    ): NetworkResponse<MultipleArtistsResponse, ErrorResponse>

}