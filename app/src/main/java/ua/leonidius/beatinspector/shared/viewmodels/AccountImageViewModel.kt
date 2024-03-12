package ua.leonidius.beatinspector.shared.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.leonidius.beatinspector.repos.account.AccountRepository

interface AccountImageViewModel {
    fun loadAccountImage(scope: CoroutineScope)

    var pfpState: PfpState
}

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

class AccountImageViewModelImpl(
    private val accountRepository: AccountRepository
) : AccountImageViewModel {

    override var pfpState by mutableStateOf<PfpState>(PfpState.Loading)

    override fun loadAccountImage(scope: CoroutineScope) {
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

