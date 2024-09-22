package ua.leonidius.beatinspector.data.shared.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import ua.leonidius.beatinspector.data.playlists.db.PlaylistDao
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeys
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeysDao
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.db.Artist
import ua.leonidius.beatinspector.data.tracks.shared.db.Genre
import javax.inject.Singleton

@Database(
    entities = [
        PlaylistSearchResult::class,
        PlaylistPageKeys::class,

        //Genre::class,
        //Artist::class,
    ],
    version = 2,
    // exportSchema = true,
    //autoMigrations = [
    //    AutoMigration(from = 2, to = 3)
    //]
)
@Singleton
abstract class TracksDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistPageKeysDao(): PlaylistPageKeysDao

}