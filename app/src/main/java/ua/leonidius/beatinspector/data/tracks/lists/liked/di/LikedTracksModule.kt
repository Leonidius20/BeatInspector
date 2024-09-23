package ua.leonidius.beatinspector.data.tracks.lists.liked.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.leonidius.beatinspector.data.shared.db.TracksDatabase
import ua.leonidius.beatinspector.data.tracks.lists.liked.db.daos.LikedTracksDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LikedTracksModule {

    @Provides
    @Singleton
    fun provideLikedTracksDao(db: TracksDatabase): LikedTracksDao {
        return db.likedTracksDao()
    }

}