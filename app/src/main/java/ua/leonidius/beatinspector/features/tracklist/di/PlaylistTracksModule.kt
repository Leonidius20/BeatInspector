package ua.leonidius.beatinspector.features.tracklist.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.lists.playlist.PlaylistPagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class PlaylistTracksModule {

    @Binds
    @ViewModelScoped
    @Named("playlist_content")
    abstract fun bindsPlaylistTracksDataSource(
        ds: PlaylistPagingDataSource
    ): PagingDataSource<SongSearchResult>

}