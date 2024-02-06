package ua.leonidius.beatinspector.repos

import ua.leonidius.beatinspector.entities.AccountDetails

interface SpotifyAccountRepo {

    suspend fun getAccountDetails(): AccountDetails

}