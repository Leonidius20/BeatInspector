package ua.leonidius.beatinspector.data.tracks.shared.cache

import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * temporary cache for SongSearchResults which can be retrieved by id. This cache
 * is then used by the song details screen to display the song title and artist
 * (bc they are not included in the song details network response). However, once
 * database cache is fully implemented, this cache will be removed.
 */
@Singleton
class SongTitlesInMemCache @Inject constructor(): InMemCache<String, SongSearchResult> {

    override val cache = mutableMapOf<String, SongSearchResult>()

}