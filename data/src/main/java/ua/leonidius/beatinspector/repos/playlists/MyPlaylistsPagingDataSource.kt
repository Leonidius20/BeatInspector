package ua.leonidius.beatinspector.repos.playlists

import ua.leonidius.beatinspector.datasources.network.dto.responses.MyPlaylistsResponse
import ua.leonidius.beatinspector.datasources.network.services.MyPlaylistsService
import ua.leonidius.beatinspector.entities.PlaylistSearchResult
import ua.leonidius.beatinspector.repos.BasePagingDataSource

class MyPlaylistsPagingDataSource(
    private val service: MyPlaylistsService,
): BasePagingDataSource<MyPlaylistsResponse, PlaylistSearchResult>(
    service::getMyPlaylists
)