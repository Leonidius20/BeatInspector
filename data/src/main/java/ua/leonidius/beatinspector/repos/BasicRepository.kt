package ua.leonidius.beatinspector.repos

interface BasicRepository<I, T> {

    suspend fun get(id: I): T

}