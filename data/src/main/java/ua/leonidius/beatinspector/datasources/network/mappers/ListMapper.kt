package ua.leonidius.beatinspector.datasources.network.mappers

interface ListMapper<T> {

    fun toDomainObject(): List<T>

}