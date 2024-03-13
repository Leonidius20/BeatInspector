package ua.leonidius.beatinspector.features.tracklist.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.leonidius.beatinspector.data.shared.PagingDataSource
import ua.leonidius.beatinspector.data.tracks.shared.domain.SongSearchResult
import javax.inject.Inject
import javax.inject.Named

abstract class TrackListViewModel(
    pagingSource: PagingDataSource<SongSearchResult>,
): ViewModel() {

    val flow = pagingSource.getFlow(viewModelScope)

    /*companion object {

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


    }*/

}

@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    @Named("liked") pagingSource: PagingDataSource<SongSearchResult>,
): TrackListViewModel(pagingSource)

@HiltViewModel
class RecentlyPlayedViewModel @Inject constructor(
    @Named("recent") pagingSource: PagingDataSource<SongSearchResult>,
): TrackListViewModel(pagingSource)

@HiltViewModel
class TopTracksViewModel @Inject constructor(
    @Named("top") pagingSource: PagingDataSource<SongSearchResult>,
): TrackListViewModel(pagingSource)