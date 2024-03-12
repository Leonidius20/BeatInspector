package ua.leonidius.beatinspector.data.shared

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PagingDataSource<T: Any> {

    fun getFlow(scope: CoroutineScope): Flow<PagingData<T>>

}