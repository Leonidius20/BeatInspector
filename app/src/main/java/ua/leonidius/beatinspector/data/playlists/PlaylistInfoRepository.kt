package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.repository.BasicRepository

class PlaylistInfoRepository(
    private val cache: PlaylistTitlesInMemCache,
): BasicRepository<String, PlaylistSearchResult> {

    override suspend fun get(id: String) = cache[id]

}