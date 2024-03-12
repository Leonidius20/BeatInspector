package ua.leonidius.beatinspector.data.tracks.search.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.tracks.search.network.dto.SearchResultsResponse

interface SearchApi {

    @GET("search?type=track")
    suspend fun search(
        @Query("q") q: String
    ): NetworkResponse<SearchResultsResponse, ErrorResponse>

}
