package ua.leonidius.beatinspector.repos.account

import kotlinx.coroutines.CoroutineDispatcher
import ua.leonidius.beatinspector.datasources.cache.Cache
import ua.leonidius.beatinspector.datasources.network.AccountNetworkDataSource
import ua.leonidius.beatinspector.datasources.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.entities.AccountDetails
import ua.leonidius.beatinspector.repos.BaseBasicRepository

class AccountRepositoryImpl(
    dataSource: AccountNetworkDataSource,
    cache: Cache<Unit, AccountDetails>,
    ioDispatcher: CoroutineDispatcher,
): BaseBasicRepository<Unit, AccountInfoResponse, AccountDetails>(
    cache, dataSource, ioDispatcher), AccountRepository