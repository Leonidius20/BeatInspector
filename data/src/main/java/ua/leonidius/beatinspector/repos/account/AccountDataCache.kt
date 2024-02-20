package ua.leonidius.beatinspector.repos.account

import ua.leonidius.beatinspector.datasources.cache.Cache
import ua.leonidius.beatinspector.entities.AccountDetails

interface AccountDataCache: Cache<Unit, AccountDetails>