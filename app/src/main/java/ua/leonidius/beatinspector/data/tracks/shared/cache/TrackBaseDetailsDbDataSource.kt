package ua.leonidius.beatinspector.data.tracks.shared.cache

import ua.leonidius.beatinspector.data.shared.cache.Cache
import ua.leonidius.beatinspector.data.tracks.details.db.TrackDetailsDao
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackBaseDetails
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */
@Singleton
class TrackBaseDetailsDbDataSource @Inject constructor(
    private val trackDetailsDao: TrackDetailsDao,
): Cache<String, SongSearchResult> {

    val cache = mutableMapOf<String, SongSearchResult>()

    override suspend fun get(id: String): SongSearchResult {
        return trackDetailsDao.getTrackBaseDetails(id)?.toDomainObject()
            ?: throw Exception("No data in ${this::class.simpleName} for id $id")

        // return cache[id] ?: throw Exception("No data in ${this::class.simpleName} for id $id")
    }

    @Deprecated("use batchAdd(Collection<SongSearchResult>)", ReplaceWith("batchAdd(collection)"))
    override suspend fun batchAdd(data: Map<String, SongSearchResult>) {
        // cache.putAll(data)
        batchAdd(data.values)
    }

    suspend fun batchAdd(data: Collection<SongSearchResult>) {
        trackDetailsDao.insertAllTrackBaseDetails(data.map { it.toDbObject() })
    }

    private fun SongSearchResult.toDbObject(): TrackBaseDetails {
        return TrackBaseDetails(
            trackId = this.id,
            name = this.name,
            isExplicit = this.isExplicit,
            imageUrl = this.imageUrl ?: "",
            smallestImageUrl = this.smallestImageUrl,
            artistNames = this.artistNames,
            artistIds = this.artistIds,
        )
    }

    override suspend fun has(id: String): Boolean {
        return trackDetailsDao.getTrackBaseDetails(id) != null
    }

    override suspend fun set(id: String, data: SongSearchResult) {
        trackDetailsDao.insertTrackBaseDetails(
            data.toDbObject()
        )
    }


}