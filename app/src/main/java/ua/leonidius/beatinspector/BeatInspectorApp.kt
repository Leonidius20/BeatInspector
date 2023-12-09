package ua.leonidius.beatinspector

import android.app.Application
import ua.leonidius.beatinspector.domain.usecases.SearchSongsUseCase
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl
import ua.leonidius.beatinspector.repos.retrofit.spotifyRetrofitClient

class BeatInspectorApp: Application() {

    val searchSongsUseCase = SearchSongsUseCase(
        SongsRepositoryImpl(
            BuildConfig.SPOTIFY_CLIENT_ID,
            BuildConfig.SPOTIFY_CLIENT_SECRET,
            spotifyRetrofitClient // todo: place creation somewhere else
            // actually, it may make sense to put the retrofit dependency
            // into app module, and treat app module as our infrastructure layer
            // (the layer that connects to our OS, frameworks or whatever)
            // we may need to create a wrapper interface spotifyRetrofitClient
            // and pass its implementation to the data layer, so that the data
            // layer does not depend on it? also maybe create a whole another module
            // just for retrofit-dependent stuff so as not to overload "app" module
            // with dependencies and responsibilities
        )
    )

}