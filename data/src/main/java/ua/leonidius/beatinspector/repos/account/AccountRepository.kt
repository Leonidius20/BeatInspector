package ua.leonidius.beatinspector.repos.account

import ua.leonidius.beatinspector.entities.AccountDetails

interface AccountRepository {

    suspend fun getAccountDetails(): AccountDetails

}