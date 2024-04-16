package ua.leonidius.beatinspector.data.playlists

import androidx.paging.PagingSource
import ua.leonidius.beatinspector.data.playlists.network.dto.MyPlaylistsResponse
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.shared.BasePagingDataSourceWithTitleCache
import javax.inject.Inject

class MyPlaylistsPagingDataSource @Inject constructor(
    service: MyPlaylistsService,
    cache: PlaylistTitlesInMemCache,
): BasePagingDataSourceWithTitleCache<PlaylistSearchResult, MyPlaylistsResponse>(
    service::getMyPlaylists,
    cache
)

/*class MyPlaylistsPagingDataSource @Inject constructor(

): PagingSource<String, PlaylistSearchResult>() {

    // here we will get shit from the DB
    // idk what should be the first type parameter, if its just a string then
    // i don't think we can establish the ordering of values
    // so we will probably need a synthetic int id
}*/