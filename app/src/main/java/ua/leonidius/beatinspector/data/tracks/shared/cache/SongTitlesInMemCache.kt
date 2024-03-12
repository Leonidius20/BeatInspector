package ua.leonidius.beatinspector.data.tracks.shared.cache

import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

/**
 *
 */
class SongTitlesInMemCache: InMemCache<String, SongSearchResult> {

    override val cache = mutableMapOf<String, SongSearchResult>()

}