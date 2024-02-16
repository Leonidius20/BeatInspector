package ua.leonidius.beatinspector.datasources.network.dto

data class SearchResultsResponse(
    val tracks: Tracks
) {

    data class Tracks(
        val total: Int,
        val items: List<TrackDto>
    ) {



    }







}
