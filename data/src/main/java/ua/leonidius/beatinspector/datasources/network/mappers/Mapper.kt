package ua.leonidius.beatinspector.datasources.network.mappers

interface Mapper<T> {

    fun toDomainObject(): T

}