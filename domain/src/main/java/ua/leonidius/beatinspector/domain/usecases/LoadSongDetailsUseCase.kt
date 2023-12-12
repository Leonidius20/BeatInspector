package ua.leonidius.beatinspector.domain.usecases

import ua.leonidius.beatinspector.domain.entities.SongDetails

interface LoadSongDetailsUseCase {

    suspend fun loadSongDetails(songId: String): SongDetails

}
