package ua.leonidius.beatinspector.data.shared

interface Mapper<T> {

    fun toDomainObject(): T

}