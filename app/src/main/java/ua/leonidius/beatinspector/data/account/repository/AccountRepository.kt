package ua.leonidius.beatinspector.data.account.repository

import ua.leonidius.beatinspector.data.account.domain.AccountDetails
import ua.leonidius.beatinspector.data.shared.repository.BasicRepository

interface AccountRepository: BasicRepository<Unit, AccountDetails>