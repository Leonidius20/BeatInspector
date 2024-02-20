package ua.leonidius.beatinspector.datasources.cache

import ua.leonidius.beatinspector.entities.PlaylistSearchResult

class MyPlaylistsCache: Cache<Any, List<PlaylistSearchResult>> {

    private var cache: List<PlaylistSearchResult>? = null

    override fun retrieve(id: Any): List<PlaylistSearchResult> {
        return cache!!
    }

    override fun store(id: Any, data: List<PlaylistSearchResult>) {
        cache = data
    }

    override fun has(id: Any): Boolean {
        return cache != null
    }

    override fun clear() {
        cache = null
    }

}