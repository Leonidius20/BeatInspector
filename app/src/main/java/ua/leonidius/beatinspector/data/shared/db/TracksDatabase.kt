package ua.leonidius.beatinspector.data.shared.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.leonidius.beatinspector.data.playlists.db.PlaylistDao
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeys
import ua.leonidius.beatinspector.data.playlists.db.PlaylistPageKeysDao
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import javax.inject.Singleton

@Database(
    entities = [PlaylistSearchResult::class, PlaylistPageKeys::class],
    version = 2,
    exportSchema = false,
)
@Singleton
abstract class TracksDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistPageKeysDao(): PlaylistPageKeysDao

}