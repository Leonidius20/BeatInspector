package ua.leonidius.beatinspector.data.tracks.search.repository

import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.shared.repository.BasicRepository

interface SearchRepository: BasicRepository<String, List<SongSearchResult>> {

    fun getById(id: String): SongSearchResult

}