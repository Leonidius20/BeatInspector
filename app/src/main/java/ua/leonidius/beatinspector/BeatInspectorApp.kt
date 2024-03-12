package ua.leonidius.beatinspector

import android.app.Application
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.withContext
import dagger.hilt.android.HiltAndroidApp
import dagger.internal.DaggerGenerated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepository
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepositoryImpl
import ua.leonidius.beatinspector.data.account.cache.AccountDataCache
import ua.leonidius.beatinspector.data.account.cache.AccountDataSharedPrefCache
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import ua.leonidius.beatinspector.data.account.repository.AccountRepositoryImpl
import ua.leonidius.beatinspector.data.tracks.details.cache.FullTrackDetailsCacheDataSource
import ua.leonidius.beatinspector.data.playlists.PlaylistTitlesInMemCache
import ua.leonidius.beatinspector.data.tracks.shared.cache.SongTitlesInMemCache
import ua.leonidius.beatinspector.data.account.network.AccountNetworkDataSource
import ua.leonidius.beatinspector.data.tracks.search.network.SearchNetworkDataSource
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.api.PlaylistApi
import ua.leonidius.beatinspector.data.tracks.lists.recent.network.api.RecentlyPlayedApi
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.account.network.api.AccountApi
import ua.leonidius.beatinspector.data.tracks.lists.top.network.api.TopTracksApi
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.data.playlists.MyPlaylistsPagingDataSource
import ua.leonidius.beatinspector.data.playlists.PlaylistInfoRepository
import ua.leonidius.beatinspector.data.tracks.lists.playlist.PlaylistPagingDataSource
import ua.leonidius.beatinspector.data.tracks.lists.recent.RecentlyPlayedDataSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.SavedTracksNetworkPagingSource
import ua.leonidius.beatinspector.data.tracks.lists.top.TopTracksPagingDataSource
import ua.leonidius.beatinspector.data.tracks.details.repository.TrackDetailsRepository
import ua.leonidius.beatinspector.data.tracks.details.repository.TrackDetailsRepositoryImpl
import ua.leonidius.beatinspector.data.settings.SettingsStore
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.shared.logic.eventbus.Event
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBusImpl
import java.text.DecimalFormat

@HiltAndroidApp
class BeatInspectorApp: Application() {

    //lateinit var authenticator: Authenticator

    lateinit var searchRepository: SearchRepository

    lateinit var accountRepository: AccountRepository

    val decimalFormat = DecimalFormat("0.##") // for bpm and loudness

    var isSpotifyInstalled = false

    lateinit var accountDataCache: AccountDataCache

    lateinit var libraries: List<Library>

    lateinit var licenses: Set<License>

    lateinit var trackDetailsRepository: TrackDetailsRepository

    // val Context.authStateDataStore: DataStore<Preferences> by preferencesDataStore(name = getString(R.string.preferences_tokens_file_name))

    lateinit var savedTracksNetworkPagingSource: SavedTracksNetworkPagingSource

    lateinit var myPlaylistsPagingDataSource: PagingDataSource<PlaylistSearchResult>

    lateinit var recentlyPlayedDataSource: PagingDataSource<SongSearchResult>

    lateinit var playlistDataSourceFactory: (String) -> PlaylistPagingDataSource

    lateinit var topTracksDataSource: PagingDataSource<SongSearchResult>

    lateinit var settingsStore: SettingsStore

    lateinit var playlistInfoRepository: PlaylistInfoRepository

    private val settingsDs: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )

    lateinit var eventBusO: MutableSharedFlow<Event>

    lateinit var eventBus: EventBus

    override fun onCreate() {
        super.onCreate()

        /*val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            this,
            getString(R.string.preferences_tokens_file_name),
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )*/

        eventBusO = MutableSharedFlow()
        val collectEventsScope = MainScope()

        eventBus = EventBusImpl


        settingsStore = SettingsStore(
            settingsDs,
            eventBusO,
            collectEventsScope,
        )

        val hideExplicit = settingsStore.hideExplicitFlow
         //val hideExplicit = flowOf(false)

        /*authenticator = Authenticator(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthStateSharedPrefStorage(sharedPreferences),
            AuthorizationService(this), // todo: research GC for this (dispose() method?)
            eventBusO,
        )*/



        //val authInterceptor = AuthInterceptor(authenticator)

        /*val okHttpCache = Cache(
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
                /*.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                })*/
                .build())
            .build()*/


       /* val searchService = retrofit.create(SearchService::class.java)
        val audioAnalysisService = retrofit.create(TrackAudioAnalysisService::class.java)
        val artistsService = retrofit.create(ArtistsService::class.java)*/

        //val searchNetworkDataSource = SearchNetworkDataSource(searchService)
        val searchCacheDataSource = SongTitlesInMemCache()

        searchRepository = SearchRepositoryImpl(Dispatchers.IO, searchNetworkDataSource, searchCacheDataSource, hideExplicit)

        val trackDetailsCacheDataSource = FullTrackDetailsCacheDataSource()

        trackDetailsRepository = TrackDetailsRepositoryImpl(
            trackDetailsCacheDataSource,
            searchRepository,
            artistsService,
            audioAnalysisService,
            Dispatchers.IO
        )

        val spotifyAccountService = retrofit.create(AccountApi::class.java)

        accountDataCache = AccountDataSharedPrefCache(
            getSharedPreferences(getString(R.string.preferences_account_data_file_name), MODE_PRIVATE),
            eventBusO,
        )


        accountRepository = AccountRepositoryImpl(AccountNetworkDataSource(spotifyAccountService), accountDataCache, Dispatchers.IO)

        // check if Spotify is installed
        isSpotifyInstalled = isPackageInstalled("com.spotify.music")

        val libs = Libs.Builder()
            .withContext(this)
            .build()

        libraries = libs.libraries
        licenses = libs.licenses

        val savedTracksService = retrofit.create(LikedTracksApi::class.java)

        savedTracksNetworkPagingSource = SavedTracksNetworkPagingSource(
            savedTracksService, searchCacheDataSource, hideExplicit)

        val myPlaylistService = retrofit.create(MyPlaylistsService::class.java)
        val playlistTitleCache = PlaylistTitlesInMemCache()
        myPlaylistsPagingDataSource = MyPlaylistsPagingDataSource(
            myPlaylistService, playlistTitleCache)

        val recentlyPlayedApi = retrofit.create(RecentlyPlayedApi::class.java)
        recentlyPlayedDataSource = RecentlyPlayedDataSource(
            recentlyPlayedApi, searchCacheDataSource, hideExplicit)

        val playlistApi = retrofit.create(PlaylistApi::class.java)
        playlistDataSourceFactory = { playlistId ->
            PlaylistPagingDataSource(
                playlistApi, searchCacheDataSource, playlistId, hideExplicit)
        }

        val topTracksApi = retrofit.create(TopTracksApi::class.java)
        topTracksDataSource = TopTracksPagingDataSource(
            topTracksApi, searchCacheDataSource, hideExplicit)

        playlistInfoRepository = PlaylistInfoRepository(playlistTitleCache)
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