package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse
import ua.leonidius.beatinspector.datasources.network.dto.TrackAudioAnalysisResponse

interface TrackAudioAnalysisService {

    @GET("audio-analysis/{id}")
    suspend fun getTrackAudioAnalysis(
        @Path("id") trackId: String
    ): NetworkResponse<TrackAudioAnalysisResponse, ErrorResponse>

}