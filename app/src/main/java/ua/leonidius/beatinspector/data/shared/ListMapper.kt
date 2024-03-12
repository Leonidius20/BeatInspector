package ua.leonidius.beatinspector.data.shared

interface ListMapper<T> {

    fun toDomainObject(): List<T>

}