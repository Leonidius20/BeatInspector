package ua.leonidius.beatinspector

import android.app.Application
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.auth.Authenticator
import ua.leonidius.beatinspector.domain.entities.SongDetails
import ua.leonidius.beatinspector.domain.usecases.LoadSongDetailsUseCase
import ua.leonidius.beatinspector.domain.usecases.SearchSongsUseCase
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl
import ua.leonidius.beatinspector.repos.retrofit.AuthInterceptor
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class BeatInspectorApp: Application() {

    lateinit var authenticator: Authenticator



    lateinit var searchSongsUseCase: SearchSongsUseCase

    lateinit var songDetailsUseCase: LoadSongDetailsUseCase

    override fun onCreate() {
        super.onCreate()
        authenticator = Authenticator(BuildConfig.SPOTIFY_CLIENT_ID, this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory. create())
            .client(
                OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(authenticator)) // todo: creating object in other place, in "app"
                .build())
            .build()

        val spotifyRetrofitClient = retrofit.create(SpotifyRetrofitClient::class.java)

        searchSongsUseCase = SearchSongsUseCase(
            SongsRepositoryImpl(
                spotifyRetrofitClient
                // todo
                // we may need to create a wrapper interface spotifyRetrofitClient
                // and pass its implementation to the data layer, so that the data
                // layer does not depend on it? also maybe create a whole another module
                // just for retrofit-dependent stuff so as not to overload "app" module
                // with dependencies and responsibilities
            )
        )

        songDetailsUseCase = object : LoadSongDetailsUseCase {

            override suspend fun loadSongDetails(songId: String): SongDetails {
                return SongDetails(
                    "Pacifier",
                    arrayOf("Baby Gronk"),
                    420.0,
                    "C Maj"
                )
            }

        }
    }


    // todo: maybe split Authenticator class and AuthState and only create
    // Authenticator when needed?

}