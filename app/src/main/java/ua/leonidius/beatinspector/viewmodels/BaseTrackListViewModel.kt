package ua.leonidius.beatinspector.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.PagingSource
import ua.leonidius.beatinspector.entities.SongSearchResult

class BaseTrackListViewModel(
    private val source: PagingSource<Int, SongSearchResult>,
): ViewModel() {
}