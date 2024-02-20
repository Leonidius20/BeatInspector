package ua.leonidius.beatinspector.datasources.network

import ua.leonidius.beatinspector.datasources.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.datasources.network.services.SpotifyAccountService
import ua.leonidius.beatinspector.entities.AccountDetails

class AccountNetworkDataSource(
    private val spotifyAccountService: SpotifyAccountService,
): BaseNetworkDataSource<Unit, AccountInfoResponse, AccountDetails>(

    service = { _ -> spotifyAccountService.getAccountInfo() }

)