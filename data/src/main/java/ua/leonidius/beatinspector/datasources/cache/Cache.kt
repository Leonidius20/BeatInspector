package ua.leonidius.beatinspector.datasources.cache

/**
 * @param T - type of the data to be cached
 * @param I - type of the ID of the data, can be Unit if there's only 1 object to store
 */
interface Cache<I, T> {

    fun retrieve(id: I): T

    fun store(id: I, data: T)

    fun has(id: I): Boolean

    fun clear()


}