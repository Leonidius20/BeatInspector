package ua.leonidius.beatinspector.repos.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SpotifyRetrofitClient {

    @GET("search?type=track")
    suspend fun searchForSongs(
        @Query("q") q: String
    ): Response<SpotifySearchResult>

    @GET("audio-analysis/{id}")
    suspend fun getTrackDetails(
        @Path("id") trackId: String
    ): Response<SpotifyTrackAnalysisResponse>

    data class TrackResponse(
        val id: String,
        val artists: List<Artist>,
    ) {
        data class Artist(
            val name: String,
            val id: String,
        )
    }

    @GET("tracks/{id}")
    suspend fun getTrack(
        @Path("id") trackId: String
    ): Response<TrackResponse>

    data class ArtistResponse(
        val id: String,
        val name: String,
        val genres: List<String>,
    )

    @GET("artists/{id}")
    suspend fun getArtist(
        @Path("id") artistId: String
    ): Response<ArtistResponse>

}