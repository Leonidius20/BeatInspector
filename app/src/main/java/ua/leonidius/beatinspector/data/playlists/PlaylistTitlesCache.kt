package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistTitlesInMemCache @Inject constructor(): InMemCache<String, PlaylistSearchResult> {

    override val cache = mutableMapOf<String, PlaylistSearchResult>()

}