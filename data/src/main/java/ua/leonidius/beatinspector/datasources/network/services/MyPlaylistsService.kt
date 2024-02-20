package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.responses.MyPlaylistsResponse

interface MyPlaylistsService {

    @GET("me/playlists")
    suspend fun getMyPlaylists(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): NetworkResponse<MyPlaylistsResponse, ErrorResponse>

}