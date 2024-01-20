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
            val album: Album,
            val artists: List<Artist>
        ) {

            fun artistsListToString() = artists.map { it.name }.joinToString(", ")

        }

    }

    data class Artist(
        val id: String,
        val name: String,
    )

    data class Album(
        val id: String,
        val images: List<Image> // first image is the biggest one
    )

    data class Image(
        val url: String,
    )

}
