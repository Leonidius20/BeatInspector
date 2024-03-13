package ua.leonidius.beatinspector.data.tracks.details.repository

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.leonidius.beatinspector.data.tracks.details.cache.FullTrackDetailsCacheDataSource
import ua.leonidius.beatinspector.data.tracks.details.domain.Song
import ua.leonidius.beatinspector.data.tracks.details.network.api.ArtistsApi
import ua.leonidius.beatinspector.data.tracks.details.network.api.TrackAudioAnalysisApi
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepository
import ua.leonidius.beatinspector.data.shared.network.toUIException
import javax.inject.Inject
import javax.inject.Named

class TrackDetailsRepositoryImpl @Inject constructor(
    private val trackDetailsCacheDataSource: FullTrackDetailsCacheDataSource,
    private val searchRepository: SearchRepository, // for title and artists
    private val artistsApi: ArtistsApi,
    private val audioAnalysisService: TrackAudioAnalysisApi,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
): TrackDetailsRepository {

    override suspend fun getFullDetails(id: String): Song = withContext(ioDispatcher) {
        trackDetailsCacheDataSource.getFromCache(id)
            ?.let { return@withContext it }

        val baseInfo = searchRepository.getById(id)

        val trackAnalysisDeferredResponse = async {
            when (val response = audioAnalysisService.getTrackAudioAnalysis(baseInfo.id)) {
                is NetworkResponse.Success -> Result.success(response.body.track)

                is NetworkResponse.Error -> Result.failure(response.toUIException())
            }
        }

        val genresDeferredResponse = async {
            when (val response = artistsApi.getArtists(baseInfo.artists.joinToString(",") { it.id })) {
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

    private fun assembleTrackDomainObject(
        baseInfo: ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult,
        details: ua.leonidius.beatinspector.data.tracks.details.network.dto.TrackAudioAnalysisDto,
        genres: List<String>,
    ): ua.leonidius.beatinspector.data.tracks.details.domain.Song {

        return ua.leonidius.beatinspector.data.tracks.details.domain.Song(
            id = baseInfo.id,
            name = baseInfo.name,
            artist = baseInfo.artists.joinToString(", ") { it.name }, // todo: don't, just return as is and let ui layer handle it
            duration = details.duration,
            loudness = details.loudness,
            bpm = details.tempo,
            bpmConfidence = details.tempoConfidence,
            timeSignature = details.timeSignature,
            timeSignatureConfidence = details.timeSignatureConfidence,
            key = getKeyStringFromSpotifyValue(details.key, details.mode),
            keyConfidence = details.keyConfidence,
            modeConfidence = details.modeConfidence,
            genres = genres,
            albumArtUrl = baseInfo.imageUrl,
        )
    }

    private fun getKeyStringFromSpotifyValue(keyInt: Int, modeInt: Int): String {
        val key = when (keyInt) {
            0 -> "C"
            1 -> "C♯/D♭"
            2 -> "D"
            3 -> "D♯/E♭"
            4 -> "E"
            5 -> "F"
            6 -> "F♯/G♭"
            7 -> "G"
            8 -> "G♯/A♭"
            9 -> "A"
            10 -> "A♯/B♭"
            11 -> "B"
            else -> "?"
        }

        val mode = when (modeInt) {
            1 -> "Maj"
            0 -> "Min"
            else -> "?"

        }

        return "$key $mode"
    }


}