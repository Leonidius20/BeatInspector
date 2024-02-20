package ua.leonidius.beatinspector.datasources.network.mappers

interface ListMapper<T> {

    fun toDomainList(): List<T>

}