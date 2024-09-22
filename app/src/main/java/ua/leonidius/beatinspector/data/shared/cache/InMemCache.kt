package ua.leonidius.beatinspector.data.shared.cache

/**
 * @param T - type of the data to be cached
 * @param I - type of the ID of the data, can be Unit if there's only 1 object to store
 */
interface Cache<I, T> {

    suspend fun get(id: I): T

    suspend fun set(id: I, data: T)

    suspend fun batchAdd(data: Map<I, T>)

    suspend fun has(id: I): Boolean

}


interface InMemCache<I, T> : Cache<I, T> {

    val cache: MutableMap<I, T>

    override suspend fun get(id: I): T {
        return cache[id] ?: throw Exception("No data in ${this::class.simpleName} for id $id")
    }

    override suspend fun set(id: I, data: T) {
        cache[id] = data
    }

    override suspend fun batchAdd(data: Map<I, T>) {
        cache.putAll(data)
    }

    override suspend fun has(id: I): Boolean {
        return cache.containsKey(id)
    }

}