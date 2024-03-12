package ua.leonidius.beatinspector.datasources.network

import ua.leonidius.beatinspector.datasources.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.datasources.network.services.SpotifyAccountService
import ua.leonidius.beatinspector.data.account.domain.AccountDetails

class AccountNetworkDataSource(
    private val spotifyAccountService: SpotifyAccountService,
): BaseNetworkDataSource<Unit, AccountInfoResponse, ua.leonidius.beatinspector.data.account.domain.AccountDetails>(

    service = { _ -> spotifyAccountService.getAccountInfo() }

)