package ua.leonidius.beatinspector.data.playlists.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.playlists.network.dto.MyPlaylistsResponse

interface MyPlaylistsService {

    @GET("me/playlists")
    suspend fun getMyPlaylists(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): NetworkResponse<MyPlaylistsResponse, ErrorResponse>

}