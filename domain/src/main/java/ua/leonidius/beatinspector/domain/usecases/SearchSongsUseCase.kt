package ua.leonidius.beatinspector.domain.usecases

import ua.leonidius.beatinspector.domain.entities.SongSearchResult
import ua.leonidius.beatinspector.domain.repositories.SongsRepository

class SearchSongsUseCase(val repository: SongsRepository) {

    // todo: create an interface and an impl
    suspend fun searchSongs(query: String): List<SongSearchResult> {
        return listOf(SongSearchResult("1","Everything", "Pnp"), SongSearchResult("2","Uhh", "oof"))
        // todo put it in repository impl
    }

}