package ua.leonidius.beatinspector.domain.usecases

import ua.leonidius.beatinspector.domain.entities.SongDetails
import ua.leonidius.beatinspector.domain.repositories.SongsRepository

class LoadSpotifySongDetailsUseCase(
    private val repository: SongsRepository
): LoadSongDetailsUseCase {
    override suspend fun loadSongDetails(songId: String): SongDetails {
        return repository.getTrackDetails(songId)
    }

}