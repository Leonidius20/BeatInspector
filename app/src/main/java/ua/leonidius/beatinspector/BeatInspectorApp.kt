package ua.leonidius.beatinspector

import android.app.Application
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.auth.Authenticator
import ua.leonidius.beatinspector.repos.SongsRepository
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSourceImpl
import ua.leonidius.beatinspector.repos.retrofit.AuthInterceptor
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient

class BeatInspectorApp: Application() {

    lateinit var authenticator: Authenticator

    lateinit var songsRepository: SongsRepository

    override fun onCreate() {
        super.onCreate()
        authenticator = Authenticator(BuildConfig.SPOTIFY_CLIENT_ID, this)

        val authInterceptor = AuthInterceptor(authenticator)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory. create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(
                OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build())
            .build()

        val spotifyRetrofitClient = retrofit.create(SpotifyRetrofitClient::class.java)

        val songsInMemCache = SongsInMemCache()
        val networkDataSource = SongsNetworkDataSourceImpl(spotifyRetrofitClient)

        songsRepository = SongsRepositoryImpl(spotifyRetrofitClient, songsInMemCache, networkDataSource)
    }

}