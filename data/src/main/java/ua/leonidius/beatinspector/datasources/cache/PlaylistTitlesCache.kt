package ua.leonidius.beatinspector.datasources.cache

import ua.leonidius.beatinspector.entities.PlaylistSearchResult

class PlaylistTitlesInMemCache: InMemCache<String, PlaylistSearchResult> {

    override val cache = mutableMapOf<String, PlaylistSearchResult>()

}