package ua.leonidius.beatinspector.repos.search

import ua.leonidius.beatinspector.SongDataIOException
import ua.leonidius.beatinspector.entities.SongSearchResult
import ua.leonidius.beatinspector.repos.BasicRepository

interface SearchRepository: BasicRepository<String, List<SongSearchResult>> {

    fun getById(id: String): SongSearchResult

}