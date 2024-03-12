package ua.leonidius.beatinspector.data.tracks.search.network

import ua.leonidius.beatinspector.data.shared.network.BaseNetworkDataSource
import ua.leonidius.beatinspector.data.tracks.search.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.data.tracks.search.network.api.SearchApi
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import javax.inject.Inject

class SearchNetworkDataSource @Inject constructor(
    private val searchService: SearchApi
): BaseNetworkDataSource<String, SearchResultsResponse, List<SongSearchResult>>(

    service = { query -> searchService.search(query) }

)