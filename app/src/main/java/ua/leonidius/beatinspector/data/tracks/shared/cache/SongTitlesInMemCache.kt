package ua.leonidius.beatinspector.data.tracks.shared.cache

import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import javax.inject.Inject

/**
 *
 */
class SongTitlesInMemCache @Inject constructor(): InMemCache<String, SongSearchResult> {

    override val cache = mutableMapOf<String, SongSearchResult>()

}