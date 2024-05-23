package ua.leonidius.beatinspector.features.shared.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.data.account.repository.AccountRepository
import javax.inject.Inject

/**
 * profile picture state
 */
sealed class PfpState {

    data class Loaded(
        val imageUrl: String
    ) : PfpState()

    sealed class NotLoaded : PfpState()

    object ErrorLoading : NotLoaded()

    object Loading : NotLoaded()

    object NoImageOnAccount : NotLoaded()
}

@HiltViewModel
class AccountImageViewModel @Inject constructor(
    private val accountRepository: AccountRepository
): ViewModel() {

    var pfpState by mutableStateOf<PfpState>(PfpState.Loading)

    init {
        loadAccountImage(viewModelScope)
    }

    private fun loadAccountImage(scope: CoroutineScope) {
        scope.launch {
            pfpState = PfpState.Loading

            pfpState = try {
                val accountImageUrl = accountRepository.get(Unit).smallImageUrl
                if (accountImageUrl != null) {
                    PfpState.Loaded(accountImageUrl)
                } else {
                    PfpState.NoImageOnAccount
                }
            } catch (e: Exception) {
                PfpState.ErrorLoading
            }
        }
    }

}

