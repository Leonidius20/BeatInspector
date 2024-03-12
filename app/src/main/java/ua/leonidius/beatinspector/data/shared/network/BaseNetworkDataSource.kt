package ua.leonidius.beatinspector.data.shared.network

import com.haroldadmin.cnradapter.NetworkResponse
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse

/**
 * The goal of this class is to load data from service and convert it to domain object
 *
 * @param I - type of the ID of the data to be retrieved
 * @param D - type of the DTO that can be converted to the domain object
 * @param T - type of the domain object to be returned
 */
open class BaseNetworkDataSource<I, D: ua.leonidius.beatinspector.data.shared.Mapper<T>, T>(
    private val service: suspend (I) -> NetworkResponse<D, ErrorResponse>,
): NetworkDataSource<I, D, T> {

    override suspend fun load(id: I): T {
        return when (val result = service(id)) {
            is NetworkResponse.Success<D, ErrorResponse> -> {
                result.body.toDomainObject()
            }

            is NetworkResponse.Error<D, ErrorResponse> -> {
                throw result.toUIException()
            }
        }
    }

}