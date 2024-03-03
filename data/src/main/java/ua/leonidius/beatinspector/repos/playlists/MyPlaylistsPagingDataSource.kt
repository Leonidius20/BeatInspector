package ua.leonidius.beatinspector.repos.playlists

import ua.leonidius.beatinspector.datasources.cache.PlaylistTitlesInMemCache
import ua.leonidius.beatinspector.datasources.network.dto.responses.MyPlaylistsResponse
import ua.leonidius.beatinspector.datasources.network.services.MyPlaylistsService
import ua.leonidius.beatinspector.entities.PlaylistSearchResult
import ua.leonidius.beatinspector.repos.BasePagingDataSourceWithTitleCache

class MyPlaylistsPagingDataSource(
    service: MyPlaylistsService,
    cache: PlaylistTitlesInMemCache,
): BasePagingDataSourceWithTitleCache<PlaylistSearchResult, MyPlaylistsResponse>(
    service::getMyPlaylists,
    cache
)