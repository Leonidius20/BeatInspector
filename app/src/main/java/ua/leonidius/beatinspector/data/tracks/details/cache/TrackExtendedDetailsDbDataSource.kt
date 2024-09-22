package ua.leonidius.beatinspector.data.tracks.details.cache

import ua.leonidius.beatinspector.data.tracks.details.db.TrackDetailsDao
import ua.leonidius.beatinspector.data.tracks.details.domain.Song
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackExtendedDetails
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackExtendedDetailsDbDataSource @Inject constructor(
    private val trackDetailsDao: TrackDetailsDao,
) {

   //  private val cache = mutableMapOf<String, Song>()

    suspend fun updateCache(song: Song) {
        val extendedDetails = TrackExtendedDetails(
            trackId = song.id,
            duration = song.duration,
            loudness = song.loudness,
            bpm = song.bpm,
            bpmConfidence = song.bpmConfidence,
            timeSignature = song.timeSignature,
            timeSignatureConfidence = song.timeSignatureConfidence,
            key = song.key,
            keyConfidence = song.keyConfidence,
            modeConfidence = song.modeConfidence,
            genres = song.genres
        )

        //cache[song.id] = song

        trackDetailsDao.insertTrackExtendedDetails(extendedDetails)
    }

    suspend fun getFromCache(id: String): TrackExtendedDetails? {
        // if both the title and the deatils are available
        //return cache[id]
        return trackDetailsDao.getExtendedTrackDetails(id)
    }

}