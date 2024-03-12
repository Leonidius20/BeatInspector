package ua.leonidius.beatinspector.data.shared.repository

interface BasicRepository<I, T> {

    suspend fun get(id: I): T

}