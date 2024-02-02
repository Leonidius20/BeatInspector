package ua.leonidius.beatinspector

import android.app.Application
import android.content.pm.PackageManager
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.auth.Authenticator
import ua.leonidius.beatinspector.repos.SongsRepository
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSourceImpl
import ua.leonidius.beatinspector.repos.retrofit.AuthInterceptor
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient
import java.text.DecimalFormat

class BeatInspectorApp: Application() {

    lateinit var authenticator: Authenticator

    lateinit var songsRepository: SongsRepository

    val decimalFormat = DecimalFormat("0.##") // for bpm and loudness

    var isSpotifyInstalled = false

    override fun onCreate() {
        super.onCreate()
        authenticator = Authenticator(BuildConfig.SPOTIFY_CLIENT_ID, this)

        val authInterceptor = AuthInterceptor(authenticator)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(

                OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                /*.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })*/
                .build())
            .build()

        val spotifyRetrofitClient = retrofit.create(SpotifyRetrofitClient::class.java)

        val songsInMemCache = SongsInMemCache()
        val networkDataSource = SongsNetworkDataSourceImpl(spotifyRetrofitClient, Dispatchers.IO)

        songsRepository = SongsRepositoryImpl(spotifyRetrofitClient, songsInMemCache, networkDataSource, Dispatchers.IO)

        // check if Spotify is installed
        isSpotifyInstalled = isPackageInstalled("com.spotify.music")
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}