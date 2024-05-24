package ua.leonidius.beatinspector.data.playlists.repository

import com.haroldadmin.cnradapter.NetworkResponse
import org.junit.Assert.*
import org.junit.Test
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.playlists.network.dto.MyPlaylistsResponse
import ua.leonidius.beatinspector.data.shared.network.dto.ErrorResponse

class MyPlaylistsRepositoryTest {

    object FakePlaylistsService: MyPlaylistsService {
        override suspend fun getMyPlaylists(
            limit: Int,
            offset: Int
        ): NetworkResponse<MyPlaylistsResponse, ErrorResponse> {
            TODO("Not yet implemented")
        }
    }

    object FakePlaylists

    @Test
    fun uiTest() {
      //  val repository = MyPlaylistsRepository()
    }

}