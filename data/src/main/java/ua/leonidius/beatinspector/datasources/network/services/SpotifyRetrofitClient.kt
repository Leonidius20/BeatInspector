package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ua.leonidius.beatinspector.datasources.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.datasources.network.dto.TrackAudioAnalysisResponse


interface SpotifyRetrofitClient {

    /*@GET("search?type=track")
    suspend fun searchForSongs(
        @Query("q") q: String
    ): NetworkResponse<SearchResultsResponse, SpotifyError>

    @GET("audio-analysis/{id}")
    suspend fun getTrackAudioAnalysis(
        @Path("id") trackId: String
    ): NetworkResponse<TrackAudioAnalysisResponse, SpotifyError>

    @GET("artists")
    suspend fun getArtists(
        @Query("ids") ids: String
    ): NetworkResponse<MultipleArtistsResponse, SpotifyError>*/



}