package ua.leonidius.beatinspector.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.domain.entities.SongSearchResult

class SearchViewModel : ViewModel() {

    var query by mutableStateOf("")
    private val _searchResults = mutableStateOf(emptyList<SongSearchResult>())

    val searchResults: List<SongSearchResult>
        get() = _searchResults.value

    fun performSearch() {
        viewModelScope.launch {
            _searchResults.value = listOf(SongSearchResult("Everything", "Pnp"), SongSearchResult("Uhh", "oof"))
        }
    }



}