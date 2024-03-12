package ua.leonidius.beatinspector

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import net.openid.appauth.AuthorizationService
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.leonidius.beatinspector.data.account.network.api.AccountApi
import ua.leonidius.beatinspector.data.auth.logic.Authenticator
import ua.leonidius.beatinspector.data.auth.logic.IAuthenticator
import ua.leonidius.beatinspector.data.auth.storage.AuthStateSharedPrefStorage
import ua.leonidius.beatinspector.data.playlists.network.api.MyPlaylistsService
import ua.leonidius.beatinspector.data.settings.SettingsStore
import ua.leonidius.beatinspector.data.tracks.details.network.api.ArtistsApi
import ua.leonidius.beatinspector.data.tracks.details.network.api.TrackAudioAnalysisApi
import ua.leonidius.beatinspector.data.tracks.lists.liked.network.api.LikedTracksApi
import ua.leonidius.beatinspector.data.tracks.lists.playlist.network.api.PlaylistApi
import ua.leonidius.beatinspector.data.tracks.lists.recent.network.api.RecentlyPlayedApi
import ua.leonidius.beatinspector.data.tracks.lists.top.network.api.TopTracksApi
import ua.leonidius.beatinspector.data.tracks.search.network.api.SearchApi
import ua.leonidius.beatinspector.infrastructure.AuthInterceptor
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBus
import ua.leonidius.beatinspector.shared.logic.eventbus.EventBusImpl
import ua.leonidius.beatinspector.shared.logic.settings.SettingsState
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // the objects here live as long as the app does

object MainComponent {

    @Provides
    @Singleton // only 1 such object is ever created
    fun provideAuthenticator(
        eventBus: EventBus,
        @ApplicationContext appContext: BeatInspectorApp,
        // todo how to use this ApplicationContext annotation?
    ): IAuthenticator {
        // todo: inject-annotate Authenticator constructor and let

        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            appContext,
            appContext.getString(R.string.preferences_tokens_file_name),
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return Authenticator(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthStateSharedPrefStorage(sharedPreferences),
            AuthorizationService(appContext), // todo: research GC for this (dispose() method?)
            eventBus,
        )
    }

    @Binds
    @Singleton
    fun bindEventBus(bus: EventBusImpl): EventBus { return bus }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authenticator: IAuthenticator): AuthInterceptor {
        return AuthInterceptor(authenticator)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        authInterceptor: AuthInterceptor,
        @ApplicationContext applicationContext: BeatInspectorApp // todo
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
    fun provideCoroutineIODispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun settingsFlow(
        settingsStore: SettingsStore
    ): Flow<SettingsState> {
        return settingsStore.settingsFlow
    }
}