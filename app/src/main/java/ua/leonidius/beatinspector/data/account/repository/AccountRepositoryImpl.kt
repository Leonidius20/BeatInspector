package ua.leonidius.beatinspector.data.account.repository

import kotlinx.coroutines.CoroutineDispatcher
import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.account.network.AccountNetworkDataSource
import ua.leonidius.beatinspector.data.account.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.shared.repository.BaseBasicRepository

class AccountRepositoryImpl(
    dataSource: AccountNetworkDataSource,
    cache: InMemCache<Unit, AccountDetails>,
    ioDispatcher: CoroutineDispatcher,
): BaseBasicRepository<Unit, AccountInfoResponse, AccountDetails>(
    cache, dataSource, ioDispatcher), AccountRepository