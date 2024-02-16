package ua.leonidius.beatinspector.datasources.network.services

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import ua.leonidius.beatinspector.datasources.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.datasources.network.dto.ErrorResponse

interface SpotifyAccountService {

    @GET("me")
    suspend fun getAccountInfo(): NetworkResponse<AccountInfoResponse, ErrorResponse>

}