package ua.leonidius.beatinspector.data.shared.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import ua.leonidius.beatinspector.data.playlists.db.PlaylistDao
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeys
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeysDao
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.tracks.details.db.TrackDetailsDao
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackExtendedDetails
import ua.leonidius.beatinspector.data.tracks.shared.db.TrackBaseDetails
import javax.inject.Singleton

@Database(
    entities = [
        PlaylistSearchResult::class,
        PlaylistPageKeys::class,

        TrackBaseDetails::class,
        TrackExtendedDetails::class,
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
    ]
)
@Singleton
abstract class TracksDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistPageKeysDao(): PlaylistPageKeysDao

    abstract fun trackDetailsDao(): TrackDetailsDao

}