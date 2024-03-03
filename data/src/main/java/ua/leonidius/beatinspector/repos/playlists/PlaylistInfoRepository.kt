package ua.leonidius.beatinspector.repos.playlists

import ua.leonidius.beatinspector.datasources.cache.PlaylistTitlesInMemCache
import ua.leonidius.beatinspector.entities.PlaylistSearchResult
import ua.leonidius.beatinspector.repos.BasicRepository

class PlaylistInfoRepository(
    private val cache: PlaylistTitlesInMemCache,
): BasicRepository<String, PlaylistSearchResult> {

    override suspend fun get(id: String) = cache[id]

}