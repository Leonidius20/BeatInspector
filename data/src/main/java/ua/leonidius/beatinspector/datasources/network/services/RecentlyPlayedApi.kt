package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.responses.RecentlyPlayedResponse

interface RecentlyPlayedApi {

    @GET("me/player/recently-played")
    suspend fun getRecentlyPlayed(
        @Query("limit") limit: Int = 50,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
    ): NetworkResponse<RecentlyPlayedResponse, ErrorResponse>

}