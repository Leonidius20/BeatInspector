package ua.leonidius.beatinspector.repos

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.AccountDetails
import ua.leonidius.beatinspector.repos.retrofit.SpotifyAccountService

class SpotifyAccountRepoImpl(
    private val spotifyAccountService: SpotifyAccountService,
    private val ioDispatcher: CoroutineDispatcher,
): SpotifyAccountRepo {


    // todo: move caching logic here
    override suspend fun getAccountDetails(): AccountDetails = withContext(ioDispatcher) {
        when(val response = spotifyAccountService.getAccountInfo()) {
            is NetworkResponse.Success -> {
                with(response.body) {
                    return@withContext AccountDetails(
                        id, displayName, image?.url
                    )
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