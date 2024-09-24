package ua.leonidius.beatinspector.data.tracks.lists.liked.db

import androidx.room.withTransaction
import ua.leonidius.beatinspector.data.shared.db.TracksDatabase
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.entities.LikedTrackWithPageKeys
import ua.leonidius.beatinspector.data.tracks.shared.cache.TrackBaseDetailsDbDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikedTracksDbDataSource @Inject constructor(
    private val db: TracksDatabase,
    private val trackBaseDetailsDbDataSource: TrackBaseDetailsDbDataSource,
) {

    private val likedTracksDao = db.likedTracksDao()

    companion object {
        // cache is valid only for 30 minutes.
        private val cacheLifeTime = TimeUnit.MILLISECONDS
            .convert(30, TimeUnit.MINUTES)
    }

    suspend fun isCacheFresh() =
        (System.currentTimeMillis()
                - (likedTracksDao.getCachingTimestamp() ?: 0)
                < cacheLifeTime)

    fun pagingSource() = likedTracksDao.likedTracksPagingSource()

    suspend fun clearAll() = likedTracksDao.clearAll()

    /**
     * @param clearExisting if true, remove all existing data from db table
     */
    suspend fun insertPage(
        data: List<SongSearchResult>,
        clearExisting: Boolean,
        prevPageKey: Int?,
        nextPageKey: Int?,
    ) {
        db.withTransaction {
            if (clearExisting) clearAll()

            val cachedAt = System.currentTimeMillis()

            likedTracksDao.insertAll(data.map { track ->
                LikedTrackWithPageKeys(
                    trackId = track.id,
                    prevKey = prevPageKey,
                    nextKey = nextPageKey,
                    cachedAt = cachedAt,
                )
            })

            // caching base details of these tracks (title, artists etc)
            trackBaseDetailsDbDataSource.batchAdd(data)
        }
    }

    suspend fun getItemWithPageKeysById(id: String) =
        likedTracksDao.getById(id)

}