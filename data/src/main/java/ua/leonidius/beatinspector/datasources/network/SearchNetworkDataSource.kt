package ua.leonidius.beatinspector.datasources.network

import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.datasources.network.mappers.toListOfDomainObjects
import ua.leonidius.beatinspector.datasources.network.services.SearchService
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.toUIException

class SearchNetworkDataSource(
    private val searchService: SearchService
) {

    suspend fun load(query: String): List<SongSearchResult> {
        return when (val result = searchService.search(query)) {
            is NetworkResponse.Success<SearchResultsResponse, ErrorResponse> -> {
               result.body.toListOfDomainObjects()
            }

            is NetworkResponse.Error<SearchResultsResponse, ErrorResponse> -> {
                throw result.toUIException()
            }
        }
    }


}