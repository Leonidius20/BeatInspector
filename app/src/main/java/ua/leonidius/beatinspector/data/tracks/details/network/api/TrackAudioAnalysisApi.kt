package ua.leonidius.beatinspector.data.tracks.details.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse
import ua.leonidius.beatinspector.data.tracks.details.network.dto.TrackAudioAnalysisResponse

interface TrackAudioAnalysisApi {

    @GET("audio-analysis/{id}")
    suspend fun getTrackAudioAnalysis(
        @Path("id") trackId: String
    ): NetworkResponse<TrackAudioAnalysisResponse, ErrorResponse>

}