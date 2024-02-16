package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.SavedTracksResponse

interface SavedTracksService {

    @GET("v1/me/tracks")
    suspend fun getSavedTracks(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): NetworkResponse<SavedTracksResponse, ErrorResponse>

}