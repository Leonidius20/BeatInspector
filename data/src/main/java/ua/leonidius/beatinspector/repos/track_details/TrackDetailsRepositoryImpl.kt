package ua.leonidius.beatinspector.repos.track_details

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.datasources.cache.FullTrackDetailsCacheDataSource
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.mappers.assembleTrackDomainObject
import ua.leonidius.beatinspector.datasources.network.services.ArtistsService
import ua.leonidius.beatinspector.datasources.network.services.TrackAudioAnalysisService
import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.toUIException

class TrackDetailsRepositoryImpl(
    private val trackDetailsCacheDataSource: FullTrackDetailsCacheDataSource,
    private val searchCacheDataSource: SearchCacheDataSource, // for title and artists
    private val artistsService: ArtistsService,
    private val audioAnalysisService: TrackAudioAnalysisService,
    private val ioDispatcher: CoroutineDispatcher,
): TrackDetailsRepository {

    override suspend fun getFullDetails(id: String): Song = withContext(ioDispatcher) {
        trackDetailsCacheDataSource.getFromCache(id)
            ?.let { return@withContext it }

        // try getting base info
        val baseInfo = searchCacheDataSource.getTitleInfo(id)
            ?: throw Error("no base info found in cache for song id $id")

        val trackAnalysisDeferredResponse = async {
            when (val response = audioAnalysisService.getTrackAudioAnalysis(baseInfo.id)) {
                is NetworkResponse.Success -> Result.success(response.body.track)

                is NetworkResponse.Error -> Result.failure(response.toUIException())
            }
        }

        val genresDeferredResponse = async {
            when (val response = artistsService.getArtists(baseInfo.artists.joinToString(",") { it.id })) {
                is NetworkResponse.Success -> Result.success(
                    response.body.artists.map { it.genres }.flatten().distinct())

                is NetworkResponse.Error -> Result.failure(response.toUIException())
                // ??? throw response.toUIException()
            }
        }

        val trackAnalysis = trackAnalysisDeferredResponse.await().getOrThrow()

        // if genres request fails and we returned an empty list instead,
        // if would be cached and we will never get those genres at all,
        // unless we clear the cache, which is not great, so we throw here
        val genres = genresDeferredResponse.await().getOrThrow()


        return@withContext assembleTrackDomainObject(
            baseInfo,
            trackAnalysis,
            genres
        ).also { trackDetailsCacheDataSource.updateCache(it) }
    }


}