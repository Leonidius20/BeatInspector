package ua.leonidius.beatinspector.domain

import ua.leonidius.beatinspector.domain.entities.SongSearchResult
import ua.leonidius.beatinspector.domain.repositories.SongsRepository

class SearchSongsUseCase(val repository: SongsRepository) {

    suspend fun searchSongs(query: String): List<SongSearchResult> {
        return emptyList()
    }

}