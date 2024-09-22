package ua.leonidius.beatinspector.data.tracks.details.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackExtendedDetails
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackBaseDetails

@Dao
interface TrackDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackBaseDetails(details: TrackBaseDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrackBaseDetails(details: List<TrackBaseDetails>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackExtendedDetails(details: TrackExtendedDetails)

    @Query("select * from tracks where trackId = :trackId")
    suspend fun getTrackBaseDetails(trackId: String): TrackBaseDetails?

    @Query("select * from track_audio_details where trackId = :trackId")
    suspend fun getExtendedTrackDetails(trackId: String): TrackExtendedDetails?

}