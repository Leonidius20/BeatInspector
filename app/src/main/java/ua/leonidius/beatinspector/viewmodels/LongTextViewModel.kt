package ua.leonidius.beatinspector.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class LongTextViewModel(
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    val textId = savedStateHandle.get<String>("textId")!!.toInt()

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
               return LongTextViewModel(
                    extras.createSavedStateHandle(),
                ) as T
            }

        }

    }


}