package ua.leonidius.beatinspector.data.playlists

import ua.leonidius.beatinspector.data.playlists.network.dto.MyPlaylistsResponse
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.BasePagingDataSourceWithTitleCache

class MyPlaylistsPagingDataSource(
    service: MyPlaylistsService,
    cache: PlaylistTitlesInMemCache,
): BasePagingDataSourceWithTitleCache<PlaylistSearchResult, MyPlaylistsResponse>(
    service::getMyPlaylists,
    cache
)