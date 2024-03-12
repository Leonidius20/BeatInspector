package ua.leonidius.beatinspector.data.account.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import ua.leonidius.beatinspector.data.account.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse

interface AccountApi {

    @GET("me")
    suspend fun getAccountInfo(): NetworkResponse<AccountInfoResponse, ErrorResponse>

}