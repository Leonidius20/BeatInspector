package ua.leonidius.beatinspector

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BeatInspectorApp: Application() {

    //lateinit var authenticator: Authenticator

    // lateinit var searchRepository: SearchRepository

    // lateinit var accountRepository: AccountRepository

    // val decimalFormat = DecimalFormat("0.##") // for bpm and loudness

   // var isSpotifyInstalled = false

   // lateinit var accountDataCache: AccountDataCache

    // lateinit var libraries: List<Library>

   // lateinit var licenses: Set<License>

   // lateinit var trackDetailsRepository: TrackDetailsRepository

    // val Context.authStateDataStore: DataStore<Preferences> by preferencesDataStore(name = getString(R.string.preferences_tokens_file_name))

   // lateinit var savedTracksNetworkPagingSource: SavedTracksNetworkPagingSource

   // lateinit var myPlaylistsPagingDataSource: PagingDataSource<PlaylistSearchResult>

    //lateinit var recentlyPlayedDataSource: PagingDataSource<SongSearchResult>

   // lateinit var playlistDataSourceFactory: (String) -> PlaylistPagingDataSource

    //lateinit var topTracksDataSource: PagingDataSource<SongSearchResult>

    //lateinit var settingsStore: SettingsRepository

    //lateinit var playlistInfoRepository: PlaylistInfoRepository

    /*val settingsDs: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )*/

    //lateinit var eventBusO: MutableSharedFlow<Event>

    //lateinit var eventBus: EventBus

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

        //eventBusO = MutableSharedFlow()
        //val collectEventsScope = MainScope()

        //eventBus = EventBusImpl


        /*settingsStore = SettingsRepository(
            settingsDs,
            eventBusO,
            collectEventsScope,
        )*/

        //val hideExplicit = settingsStore.hideExplicitFlow
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
        //val searchCacheDataSource = SongTitlesInMemCache()

        //searchRepository = SearchRepositoryImpl(Dispatchers.IO, searchNetworkDataSource, searchCacheDataSource, hideExplicit)

       /* val trackDetailsCacheDataSource = FullTrackDetailsCacheDataSource()

        trackDetailsRepository = TrackDetailsRepositoryImpl(
            trackDetailsCacheDataSource,
            searchRepository,
            artistsService,
            audioAnalysisService,
            Dispatchers.IO
        )*/

       /* val spotifyAccountService = retrofit.create(AccountApi::class.java)

        accountDataCache = AccountDataCache(
            getSharedPreferences(getString(R.string.preferences_account_data_file_name), MODE_PRIVATE),
            eventBusO,
        )


        accountRepository = AccountRepositoryImpl(AccountNetworkDataSource(spotifyAccountService), accountDataCache, Dispatchers.IO)

        // check if Spotify is installed
        isSpotifyInstalled = isPackageInstalled("com.spotify.music")*/

       /* val libs = Libs.Builder()
            .withContext(this)
            .build()

        libraries = libs.libraries*/
       /* licenses = libs.licenses

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

        playlistInfoRepository = PlaylistInfoRepository(playlistTitleCache)*/
    }

    // todo: maybe have it return flow if there's a lib for that?


}

fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}