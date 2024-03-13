package ua.leonidius.beatinspector.data.account.repository

import kotlinx.coroutines.CoroutineDispatcher
import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.account.network.AccountNetworkDataSource
import ua.leonidius.beatinspector.data.account.network.dto.AccountInfoResponse
import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.shared.repository.BaseBasicRepository
import javax.inject.Inject
import javax.inject.Named

class AccountRepositoryImpl @Inject constructor(
    dataSource: AccountNetworkDataSource,
    cache: InMemCache<Unit, AccountDetails>,
    @Named("io") ioDispatcher: CoroutineDispatcher,
): BaseBasicRepository<Unit, AccountInfoResponse, AccountDetails>(
    cache, dataSource, ioDispatcher), AccountRepository