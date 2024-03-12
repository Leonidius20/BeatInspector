package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.datasources.network.dto.responses.MyPlaylistsResponse
import ua.leonidius.beatinspector.datasources.network.services.MyPlaylistsService
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.BasePagingDataSourceWithTitleCache

class MyPlaylistsPagingDataSource(
    service: MyPlaylistsService,
    cache: PlaylistTitlesInMemCache,
): BasePagingDataSourceWithTitleCache<PlaylistSearchResult, MyPlaylistsResponse>(
    service::getMyPlaylists,
    cache
)