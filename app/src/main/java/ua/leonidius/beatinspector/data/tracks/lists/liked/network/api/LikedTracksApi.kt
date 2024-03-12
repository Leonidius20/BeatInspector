package ua.leonidius.beatinspector.data.tracks.lists.liked.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.dto.LikedTracksResponse

interface LikedTracksApi {

    @GET("me/tracks")
    suspend fun getSavedTracks(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): NetworkResponse<LikedTracksResponse, ErrorResponse>

}