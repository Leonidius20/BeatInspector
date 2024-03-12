package ua.leonidius.beatinspector.data.tracks.details.cache

import ua.leonidius.beatinspector.data.tracks.details.domain.Song

class FullTrackDetailsCacheDataSource {

    // todo: proper disk cache

    private val cache = mutableMapOf<String, Song>()

    fun updateCache(song: Song) {
        cache[song.id] = song
    }

    fun getFromCache(id: String): Song? {
        return cache[id]
    }

}