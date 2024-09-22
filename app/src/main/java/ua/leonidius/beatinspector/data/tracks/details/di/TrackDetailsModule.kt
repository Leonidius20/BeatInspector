package ua.leonidius.beatinspector.data.tracks.details.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.leonidius.beatinspector.data.shared.db.TracksDatabase
import ua.leonidius.beatinspector.data.tracks.details.db.TrackDetailsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TrackDetailsModule {

    @Provides
    @Singleton
    fun bindTrackDetailsDao(db: TracksDatabase): TrackDetailsDao {
        return db.trackDetailsDao()
    }

}