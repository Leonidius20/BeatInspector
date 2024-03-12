package ua.leonidius.beatinspector.data.shared.cache

/**
 * @param T - type of the data to be cached
 * @param I - type of the ID of the data, can be Unit if there's only 1 object to store
 */
interface InMemCache<I, T> {

    val cache: MutableMap<I, T>

    operator fun get(id: I): T {
        return cache[id] ?: throw Exception("No data in ${this::class.simpleName} for id $id")
    }

    operator fun set(id: I, data: T) {
        cache[id] = data
    }

    fun batchAdd(data: Map<I, T>) {
        cache.putAll(data)
    }

    fun has(id: I): Boolean {
        return cache.containsKey(id)
    }

}