package ua.leonidius.beatinspector.data.tracks.lists.top.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.tracks.lists.top.network.dto.TopTracksResponse

interface TopTracksApi {

    @GET("me/top/tracks")
    suspend fun getTopTracks(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): NetworkResponse<TopTracksResponse, ErrorResponse>

}