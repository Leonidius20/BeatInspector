package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.responses.PlaylistResponse

interface PlaylistApi {

    @GET("playlists/{id}/tracks")
    suspend fun getTracks(
        @Path("id") playlistId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("additional_types") additionalTypes: String = "track",
        @Query("fields") fields: String = "items(track(id,name,type,artists(id,name),album(images)",
    ): NetworkResponse<PlaylistResponse, ErrorResponse>

}