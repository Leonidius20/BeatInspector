package ua.leonidius.beatinspector.datasources.network

import ua.leonidius.beatinspector.datasources.network.mappers.Mapper

/**
 * @param I - type of the ID of the data to be retrieved
 * @param D - type of the DTO that can be converted to the domain object
 * @param T - type of the domain object to be returned
 */
interface NetworkDataSource<I, D: Mapper<T>, T> {

    suspend fun load(id: I): T


}