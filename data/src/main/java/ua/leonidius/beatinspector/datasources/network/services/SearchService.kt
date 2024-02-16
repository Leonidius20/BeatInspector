package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.SearchResultsResponse

interface SearchService {

    @GET("search?type=track")
    suspend fun search(
        @Query("q") q: String
    ): NetworkResponse<SearchResultsResponse, ErrorResponse>

}
