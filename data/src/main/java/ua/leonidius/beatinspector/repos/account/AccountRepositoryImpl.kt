package ua.leonidius.beatinspector.repos.account

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.AccountDetails
import ua.leonidius.beatinspector.services.SpotifyAccountService

class AccountRepositoryImpl(
    private val spotifyAccountService: SpotifyAccountService,
    private val cache: AccountDataCache,
    private val ioDispatcher: CoroutineDispatcher,
): AccountRepository {


    // todo: move caching logic here
    override suspend fun getAccountDetails(): AccountDetails = withContext(ioDispatcher) {
        if (cache.isDataAvailable()) {
            return@withContext cache.retrieve()
        }

        when(val response = spotifyAccountService.getAccountInfo()) {
            is NetworkResponse.Success -> {
                with(response.body) {
                    val account = AccountDetails(id, displayName, smallestImage?.url, biggestImage?.url)
                    cache.store(account)
                    return@withContext account
                }
            }
            is NetworkResponse.ServerError -> {
                throw SongDataIOException.Server(response.code, response.body?.message ?: "< No response body >")
            }
            is NetworkResponse.NetworkError -> {
                throw SongDataIOException.Network(response.error)
            }
            is NetworkResponse.UnknownError -> {
                throw SongDataIOException.Unknown(response.error)
            }
        }

    }

}