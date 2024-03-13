package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.repository.BasicRepository
import javax.inject.Inject

class PlaylistInfoRepository @Inject constructor(
    private val cache: PlaylistTitlesInMemCache,
): BasicRepository<String, PlaylistSearchResult> {

    override suspend fun get(id: String) = cache[id]

}