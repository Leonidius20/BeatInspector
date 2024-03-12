package ua.leonidius.beatinspector.features.tracklist.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import ua.leonidius.beatinspector.BeatInspectorApp
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult

open class TrackListViewModel(
    pagingSource: PagingDataSource<SongSearchResult>, // todo: remove
): ViewModel() {

    val flow = pagingSource.getFlow(viewModelScope)

    companion object {

        private fun getFactoryForSource(getDataSource: (BeatInspectorApp) -> PagingDataSource<SongSearchResult>): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as BeatInspectorApp

                    return TrackListViewModel(
                        getDataSource(app),
                    ) as T
                }

            }
        }

        val SavedTracksFactory = getFactoryForSource { it.savedTracksNetworkPagingSource }

        val RecentlyPlayedFactory = getFactoryForSource { it.recentlyPlayedDataSource }

        val TopTracksFactory = getFactoryForSource { it.topTracksDataSource }


    }

}