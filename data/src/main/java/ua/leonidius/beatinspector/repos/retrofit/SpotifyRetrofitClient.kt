package ua.leonidius.beatinspector.repos.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyRetrofitClient {

    @GET("search?type=track")
    suspend fun searchForSongs(
        @Query("q") q: String
    ): Response<SpotifySearchResult>

}