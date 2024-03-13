package ua.leonidius.beatinspector

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.util.withContext
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import net.openid.appauth.AuthorizationService
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.data.account.cache.AccountDataCache
import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.account.network.api.AccountApi
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import ua.leonidius.beatinspector.data.account.repository.AccountRepositoryImpl
import ua.leonidius.beatinspector.data.auth.logic.Authenticator
import ua.leonidius.beatinspector.data.auth.logic.IAuthenticator
import ua.leonidius.beatinspector.data.auth.storage.AuthStateSharedPrefStorage
import ua.leonidius.beatinspector.data.playlists.MyPlaylistsPagingDataSource
import ua.leonidius.beatinspector.data.playlists.domain.PlaylistSearchResult
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.settings.SettingsRepository
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.shared.cache.InMemCache
import ua.leonidius.beatinspector.data.tracks.details.network.api.ArtistsApi
import ua.leonidius.beatinspector.data.tracks.details.network.api.TrackAudioAnalysisApi
import ua.leonidius.beatinspector.data.tracks.details.repository.TrackDetailsRepository
import ua.leonidius.beatinspector.data.tracks.details.repository.TrackDetailsRepositoryImpl
import ua.leonidius.beatinspector.data.tracks.lists.liked.SavedTracksNetworkPagingSource
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.playlist.PlaylistPagingDataSource
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.api.PlaylistApi
import ua.leonidius.beatinspector.data.tracks.lists.recent.RecentlyPlayedDataSource
import ua.leonidius.beatinspector.data.tracks.lists.recent.network.api.RecentlyPlayedApi
import ua.leonidius.beatinspector.data.tracks.lists.top.TopTracksPagingDataSource
import ua.leonidius.beatinspector.data.tracks.lists.top.network.api.TopTracksApi
import ua.leonidius.beatinspector.data.tracks.search.network.api.SearchApi
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepository
import ua.leonidius.beatinspector.data.tracks.search.repository.SearchRepositoryImpl
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import ua.leonidius.beatinspector.infrastructure.AuthInterceptor
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBusImpl
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState
import java.text.DecimalFormat
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // the objects here live as long as the app does

object MainModule {



    @Provides
    @Singleton
    @Named("client_id")
    fun provideClientId(): String = BuildConfig.SPOTIFY_CLIENT_ID

    @Provides
    @Singleton
    fun provideAuthService(
        @ApplicationContext appContext: Context
    ): AuthorizationService {
        return AuthorizationService(appContext)
    }



    @Provides
    @Singleton
    fun provideAuthInterceptor(authenticator: IAuthenticator): AuthInterceptor {
        return AuthInterceptor(authenticator)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        authInterceptor: AuthInterceptor,
        @ApplicationContext applicationContext: Context
    ): Retrofit {

        val okHttpCache = Cache(
            applicationContext.cacheDir,
            50 * 1024 * 1024 // 50 MB
        )

        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(
                OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .cache(okHttpCache)
                /*.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                })*/
                .build())
            .build()
    }

    @Provides
    @Singleton
    fun provideSearchService(retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAudioAnalysisService(retrofit: Retrofit): TrackAudioAnalysisApi {
        return retrofit.create(TrackAudioAnalysisApi::class.java)
    }

    @Provides
    @Singleton
    fun provideArtistsService(retrofit: Retrofit): ArtistsApi {
        return retrofit.create(ArtistsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotifyAccountService(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSavedTracksService(retrofit: Retrofit): LikedTracksApi {
        return retrofit.create(LikedTracksApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyPlaylistsService(retrofit: Retrofit): MyPlaylistsService {
        return retrofit.create(MyPlaylistsService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecentlyPlayedApi(retrofit: Retrofit): RecentlyPlayedApi {
        return retrofit.create(RecentlyPlayedApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlaylistApi(retrofit: Retrofit): PlaylistApi {
        return retrofit.create(PlaylistApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTopTracksApi(retrofit: Retrofit): TopTracksApi {
        return retrofit.create(TopTracksApi::class.java)
    }

    @Provides
    @Singleton
    @Named("io")
    fun provideCoroutineIODispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun settingsFlow(
        settingsStore: SettingsRepository
    ): Flow<SettingsState> {
        return settingsStore.settingsFlow
    }

    @Provides
    @Singleton
    @Named("general")
    fun provideSettingsDataStore(
        @ApplicationContext app: Context
    ) : DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                app.preferencesDataStoreFile("settings")
            }
        )
    }

    @Provides
    @Singleton
    @Named("account_cache")
    fun provideAccountCachePreferences(
        @ApplicationContext app: Context
    ): SharedPreferences {
        return app.getSharedPreferences(app.getString(R.string.preferences_account_data_file_name),
            Application.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    @Named("tokens_cache")
    fun provideTokensCachePreferences(
        @ApplicationContext appContext: Context
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            appContext,
            appContext.getString(R.string.preferences_tokens_file_name),
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideLibrariesList(
        libs: Libs
    ): List<Library> {
        return libs.libraries
    }

    @Provides
    @Singleton
    fun provideLicensesSet(
        libs: Libs
    ): Set<License> {
        return libs.licenses
    }

    @Provides
    @Singleton
    fun provideLibs(
        @ApplicationContext app: Context
    ): Libs {
        return Libs.Builder()
            .withContext(app)
            .build()
    }

    @Provides
    @Singleton
    fun provideDecimalFormat(): DecimalFormat {
        return DecimalFormat("0.##")
    }

    @Provides
    @Singleton
    @Named("spotify_installed")
    fun provideIsSpotifyInstalled(
        @ApplicationContext app: Context
    ): Boolean {
        return app.isPackageInstalled("com.spotify.music")
    }

    @Provides
    @Singleton
    @Named("spotify_installed_flow")
    fun provideIsSpotifyInstalledFlow(
        @Named("spotify_installed") isIt: Boolean
    ): StateFlow<Boolean> {
        return MutableStateFlow(isIt)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class EventBusModule {

    @Binds
    @Singleton
    abstract fun bindEventBus(bus: EventBusImpl): EventBus

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        accountRepository: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindAccountDataCache(
        cache: AccountDataCache
    ): InMemCache<Unit, AccountDetails>

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepository: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindTrackDetailsRepository(
        trackDetailsRepository: TrackDetailsRepositoryImpl
    ): TrackDetailsRepository

    @Binds
    @Singleton
    abstract fun bindMyPlaylistsPagingDataSource(
        myPlaylistsPagingDataSource: MyPlaylistsPagingDataSource
    ): PagingDataSource<PlaylistSearchResult>

    @Binds
    @Singleton // only 1 such object is ever created
    abstract fun bindAuthenticator(
        authenticator: Authenticator
    ): IAuthenticator

    @Binds
    @Singleton
    @Named("liked")
    abstract fun bindLikedTracksPagingDataSource(
        likedTracksPagingDataSource: SavedTracksNetworkPagingSource
    ): PagingDataSource<SongSearchResult>

    @Binds
    @Singleton
    @Named("recent")
    abstract fun bindRecentTracksPagingDataSource(
        ds: RecentlyPlayedDataSource
    ): PagingDataSource<SongSearchResult>

    @Binds
    @Singleton
    @Named("top")
    abstract fun bindTopTracksPagingDataSource(
        ds: TopTracksPagingDataSource
    ): PagingDataSource<SongSearchResult>

}