package ua.leonidius.beatinspector.datasources.cache

import ua.leonidius.beatinspector.entities.SongSearchResult

/**
 *
 */
class SongTitlesInMemCache: InMemCache<String, SongSearchResult> {

    override val cache = mutableMapOf<String, SongSearchResult>()

}