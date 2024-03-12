package ua.leonidius.beatinspector.data.shared.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.shared.network.NetworkDataSource
import ua.leonidius.beatinspector.data.shared.Mapper

/**
 * @param T - type of the data to be returned (a single object, not a list, i think)
 * @param D - type of the DTO that can be converted to the domain object
 * @param I - type of the ID of the data, can be Unit if there's only 1 object, like account details
 */
abstract class BaseBasicRepository<I, D: Mapper<T>, T>(
    private val cache: InMemCache<I, T>,
    private val networkDataSource: NetworkDataSource<I, D, T>,
    private val ioDispatcher: CoroutineDispatcher,
): BasicRepository<I, T> {

    override suspend fun get(id: I): T = withContext(ioDispatcher) {
        if (cache.has(id)) {
            return@withContext cache[id]
        }

        val data = networkDataSource.load(id)

        launch {
            cache[id] = data
        }

        return@withContext data
    }

}