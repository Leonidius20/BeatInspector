package ua.leonidius.beatinspector.data.tracks.lists.shared.db

import androidx.room.Entity

/**
 * represents a many-to-many relationship between songs and playlists.
 * Playlists also include top tracks (id 'top'), liked (id 'liked'),
 * recent (id 'recent'). The records from this table can be deleted and
 * re-fetched, because playlists can change. However, the songs themselves
 */
@Entity
class SongInPlaylist {
}