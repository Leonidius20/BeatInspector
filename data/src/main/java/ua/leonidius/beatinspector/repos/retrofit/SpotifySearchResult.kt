package ua.leonidius.beatinspector.repos.retrofit

data class SpotifySearchResult(
    val tracks: Tracks
) {

    data class Tracks(
        val total: Int,
        val items: List<Track>
    ) {

        data class Track(
            val id: String,
            val name: String,
            val artists: List<Artist>
        ) {

            fun artistsListToString() = artists.map { it.name }.joinToString(", ")

        }

    }

    data class Artist(
        val id: String,
        val name: String,
    )

}
