package ua.leonidius.beatinspector

import android.app.Application
import ua.leonidius.beatinspector.domain.usecases.SearchSongsUseCase
import ua.leonidius.beatinspector.repos.SongsRepositoryImpl

class BeatInspectorApp: Application() {
    
    val searchSongsUseCase = SearchSongsUseCase(SongsRepositoryImpl())
    
}