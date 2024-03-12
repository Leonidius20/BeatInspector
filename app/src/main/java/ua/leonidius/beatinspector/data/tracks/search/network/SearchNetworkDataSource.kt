package ua.leonidius.beatinspector.data.tracks.search.network

import ua.leonidius.beatinspector.data.shared.network.BaseNetworkDataSource
import ua.leonidius.beatinspector.data.tracks.search.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.data.tracks.search.network.api.SearchApi
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

class SearchNetworkDataSource(
    private val searchService: SearchApi
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