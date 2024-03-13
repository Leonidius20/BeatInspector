package ua.leonidius.beatinspector.data.account.network

import ua.leonidius.beatinspector.data.account.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.data.account.network.api.AccountApi
import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.shared.network.BaseNetworkDataSource
import javax.inject.Inject

class AccountNetworkDataSource @Inject constructor(
    private val accountApi: AccountApi,
): BaseNetworkDataSource<Unit, AccountInfoResponse, AccountDetails>(

    service = { _ -> accountApi.getAccountInfo() }

)