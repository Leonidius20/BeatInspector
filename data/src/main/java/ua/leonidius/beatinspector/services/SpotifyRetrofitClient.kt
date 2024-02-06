package ua.leonidius.beatinspector.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ua.leonidius.beatinspector.repos.retrofit.SpotifySearchResult
import ua.leonidius.beatinspector.repos.retrofit.SpotifyTrackAnalysisResponse


interface SpotifyRetrofitClient {

    @GET("search?type=track")
    suspend fun searchForSongs(
        @Query("q") q: String
    ): NetworkResponse<SpotifySearchResult, SpotifyError>

    @GET("audio-analysis/{id}")
    suspend fun getTrackAudioAnalysis(
        @Path("id") trackId: String
    ): NetworkResponse<SpotifyTrackAnalysisResponse, SpotifyError>

    @GET("artists")
    suspend fun getArtists(
        @Query("ids") ids: String
    ): NetworkResponse<MultipleArtistsResponse, SpotifyError>

    data class MultipleArtistsResponse(
        val artists: List<ArtistResponse>
    )

    data class ArtistResponse(
        val id: String,
        val name: String,
        val genres: List<String>
    )

    /**
     * Deserialized spotify error result
     */
    data class SpotifyError(
        val status: Int,
        val message: String,
    )

}