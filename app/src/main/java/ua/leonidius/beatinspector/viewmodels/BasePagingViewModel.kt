package ua.leonidius.beatinspector.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * @param T the type of the data that will be paged
 */
abstract class BasePagingViewModel<T : Any>(
    // base paging repo or data source
): ViewModel() {

    // abstract val flow: Flow<PagingData<T>>

}