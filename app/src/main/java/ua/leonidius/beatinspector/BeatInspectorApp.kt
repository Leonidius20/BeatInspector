package ua.leonidius.beatinspector

import android.app.Application
import android.content.pm.PackageManager
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import kotlinx.coroutines.Dispatchers
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.auth.Authenticator
import ua.leonidius.beatinspector.data.R
import ua.leonidius.beatinspector.repos.SongsRepository
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl
import ua.leonidius.beatinspector.repos.SpotifyAccountRepo
import ua.leonidius.beatinspector.repos.SpotifyAccountRepoImpl
import ua.leonidius.beatinspector.repos.datasources.SongsInMemCache
import ua.leonidius.beatinspector.repos.datasources.SongsNetworkDataSourceImpl
import ua.leonidius.beatinspector.repos.retrofit.AuthInterceptor
import ua.leonidius.beatinspector.repos.retrofit.SpotifyAccountService
import ua.leonidius.beatinspector.repos.retrofit.SpotifyRetrofitClient
import java.text.DecimalFormat

class BeatInspectorApp: Application() {

    lateinit var authenticator: Authenticator

    lateinit var songsRepository: SongsRepository

    lateinit var accountRepository: SpotifyAccountRepo

    val decimalFormat = DecimalFormat("0.##") // for bpm and loudness

    var isSpotifyInstalled = false

    private lateinit var authService: AuthorizationService

    lateinit var accountDataCache: AccountDataCache

    override fun onCreate() {
        super.onCreate()

        authService = AuthorizationService(this) // todo: research GC for this (dispose() method?)

        authenticator = Authenticator(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthStateSharedPrefStorage(getSharedPreferences(getString(
                R.string.preferences_tokens_file_name
            ), MODE_PRIVATE)), authService
        )

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

        val spotifyAccountService = retrofit.create(SpotifyAccountService::class.java)

        accountRepository = SpotifyAccountRepoImpl(spotifyAccountService, Dispatchers.IO)

        // check if Spotify is installed
        isSpotifyInstalled = isPackageInstalled("com.spotify.music")

        accountDataCache = AccountDataSharedPrefCache(getSharedPreferences(getString(ua.leonidius.beatinspector.R.string.preferences_account_data_file_name), MODE_PRIVATE))
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