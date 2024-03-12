package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult

class PlaylistTitlesInMemCache: InMemCache<String, PlaylistSearchResult> {

    override val cache = mutableMapOf<String, PlaylistSearchResult>()

}