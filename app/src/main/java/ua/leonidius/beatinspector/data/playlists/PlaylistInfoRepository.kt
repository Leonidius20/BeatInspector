package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.data.playlists.db.PlaylistDao
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.repository.BasicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistInfoRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
): BasicRepository<String, PlaylistSearchResult> {

    override suspend fun get(id: String) = playlistDao.get(id) ?:
        throw IllegalArgumentException("Playlist with id $id not found in db cache")

}