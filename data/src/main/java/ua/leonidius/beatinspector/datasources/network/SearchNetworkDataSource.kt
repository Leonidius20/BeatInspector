package ua.leonidius.beatinspector.datasources.network

import ua.leonidius.beatinspector.datasources.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.datasources.network.services.SearchService
import ua.leonidius.beatinspector.entities.SongSearchResult

class SearchNetworkDataSource(
    private val searchService: SearchService
): BaseNetworkDataSource<String, SearchResultsResponse, List<SongSearchResult>>(

    service = { query -> searchService.search(query) }

) {

    /*suspend fun load(query: String): List<SongSearchResult> {
        return when (val result = searchService.search(query)) {
            is NetworkResponse.Success<SearchResultsResponse, ErrorResponse> -> {
               result.body.toListOfDomainObjects()
            }

            is NetworkResponse.Error<SearchResultsResponse, ErrorResponse> -> {
                throw result.toUIException()
            }
        }
    }*/


}