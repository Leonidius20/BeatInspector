package ua.leonidius.beatinspector.repos.retrofit

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SpotifyRetrofitClient {

    @GET("search?type=track")
    suspend fun searchForSongs(
        @Query("q") q: String
    ): NetworkResponse<SpotifySearchResult, SpotifyError>

    @GET("audio-analysis/{id}")
    suspend fun getTrackDetails(
        @Path("id") trackId: String
    ): NetworkResponse<SpotifyTrackAnalysisResponse, SpotifyError>

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
    ): NetworkResponse<TrackResponse, SpotifyError>

    data class ArtistResponse(
        val id: String,
        val name: String,
        val genres: List<String>,
    )

    @GET("artists/{id}")
    suspend fun getArtist(
        @Path("id") artistId: String
    ): NetworkResponse<ArtistResponse, SpotifyError>

    /**
     * Deserialized spotify error result
     */
    data class SpotifyError(
        val status: Int,
        val message: String,
    )

}