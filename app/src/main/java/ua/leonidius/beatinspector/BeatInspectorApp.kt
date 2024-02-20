package ua.leonidius.beatinspector

import android.app.Application
import android.content.pm.PackageManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.withContext
import kotlinx.coroutines.Dispatchers
import net.openid.appauth.AuthorizationService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.auth.AuthStateSharedPrefStorage
import ua.leonidius.beatinspector.auth.Authenticator
import ua.leonidius.beatinspector.data.R
import ua.leonidius.beatinspector.repos.search.SearchRepository
import ua.leonidius.beatinspector.repos.search.SearchRepositoryImpl
import ua.leonidius.beatinspector.repos.account.AccountDataCache
import ua.leonidius.beatinspector.repos.account.AccountDataSharedPrefCache
import ua.leonidius.beatinspector.repos.account.AccountRepository
import ua.leonidius.beatinspector.repos.account.AccountRepositoryImpl
import ua.leonidius.beatinspector.auth.AuthInterceptor
import ua.leonidius.beatinspector.datasources.cache.FullTrackDetailsCacheDataSource
import ua.leonidius.beatinspector.datasources.cache.SearchCacheDataSource
import ua.leonidius.beatinspector.datasources.network.AccountNetworkDataSource
import ua.leonidius.beatinspector.datasources.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.datasources.network.services.ArtistsService
import ua.leonidius.beatinspector.datasources.network.services.MyPlaylistsService
import ua.leonidius.beatinspector.datasources.network.services.SavedTracksService
import ua.leonidius.beatinspector.datasources.network.services.SearchService
import ua.leonidius.beatinspector.datasources.network.services.SpotifyAccountService
import ua.leonidius.beatinspector.datasources.network.services.TrackAudioAnalysisService
import ua.leonidius.beatinspector.entities.PlaylistSearchResult
import ua.leonidius.beatinspector.repos.saved_tracks.MyPlaylistsPagingDataSource
import ua.leonidius.beatinspector.repos.saved_tracks.SavedTracksNetworkPagingSource
import ua.leonidius.beatinspector.repos.track_details.TrackDetailsRepository
import ua.leonidius.beatinspector.repos.track_details.TrackDetailsRepositoryImpl
import java.text.DecimalFormat


class BeatInspectorApp: Application() {

    lateinit var authenticator: Authenticator

    lateinit var searchRepository: SearchRepository

    lateinit var accountRepository: AccountRepository

    val decimalFormat = DecimalFormat("0.##") // for bpm and loudness

    var isSpotifyInstalled = false

    private lateinit var authService: AuthorizationService

    lateinit var accountDataCache: AccountDataCache

    lateinit var libraries: List<Library>

    lateinit var licenses: Set<License>

    lateinit var trackDetailsRepository: TrackDetailsRepository

    // val Context.authStateDataStore: DataStore<Preferences> by preferencesDataStore(name = getString(R.string.preferences_tokens_file_name))

    lateinit var savedTracksNetworkPagingSource: SavedTracksNetworkPagingSource

    lateinit var myPlaylistsPagingDataSource: PagingDataSource<PlaylistSearchResult>

    override fun onCreate() {
        super.onCreate()

        authService = AuthorizationService(this) // todo: research GC for this (dispose() method?)

        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            getString(R.string.preferences_tokens_file_name),
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )



        authenticator = Authenticator(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthStateSharedPrefStorage(sharedPreferences), authService
        )

        val authInterceptor = AuthInterceptor(authenticator)

        val okHttpCache = Cache(
            applicationContext.cacheDir,
            50 * 1024 * 1024 // 50 MB
        )

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .cache(okHttpCache)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                }).build())
            .build()


        val searchService = retrofit.create(SearchService::class.java)
        val audioAnalysisService = retrofit.create(TrackAudioAnalysisService::class.java)
        val artistsService = retrofit.create(ArtistsService::class.java)

        val searchNetworkDataSource = SearchNetworkDataSource(searchService)
        val searchCacheDataSource = SearchCacheDataSource()

        searchRepository = SearchRepositoryImpl(Dispatchers.IO, searchNetworkDataSource, searchCacheDataSource)

        val trackDetailsCacheDataSource = FullTrackDetailsCacheDataSource()

        trackDetailsRepository = TrackDetailsRepositoryImpl(
            trackDetailsCacheDataSource,
            searchCacheDataSource,
            artistsService,
            audioAnalysisService,
            Dispatchers.IO
        )

        val spotifyAccountService = retrofit.create(SpotifyAccountService::class.java)

        accountDataCache = AccountDataSharedPrefCache(getSharedPreferences(getString(ua.leonidius.beatinspector.R.string.preferences_account_data_file_name), MODE_PRIVATE))


        accountRepository = AccountRepositoryImpl(AccountNetworkDataSource(spotifyAccountService), accountDataCache, Dispatchers.IO)

        // check if Spotify is installed
        isSpotifyInstalled = isPackageInstalled("com.spotify.music")

        val libs = Libs.Builder()
            .withContext(this)
            .build()

        libraries = libs.libraries
        licenses = libs.licenses

        val savedTracksService = retrofit.create(SavedTracksService::class.java)

        savedTracksNetworkPagingSource = SavedTracksNetworkPagingSource(savedTracksService, searchCacheDataSource)

        val myPlaylistService = retrofit.create(MyPlaylistsService::class.java)
        myPlaylistsPagingDataSource = MyPlaylistsPagingDataSource(myPlaylistService)
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