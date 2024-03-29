package ua.leonidius.beatinspector.data.tracks.details.cache

import ua.leonidius.beatinspector.data.tracks.details.domain.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FullTrackDetailsCacheDataSource @Inject constructor() {

    // todo: proper disk cache

    private val cache = mutableMapOf<String, Song>()

    fun updateCache(song: Song) {
        cache[song.id] = song
    }

    fun getFromCache(id: String): Song? {
        return cache[id]
    }

}