package ua.leonidius.beatinspector.data.auth.logic

import java.io.IOException

class TokenRefreshException(
    oathProtocolErrorString: String,
    errorDescription: String,
    cause: Throwable
): IOException(
    """
        Token refresh exception. 
        Oath error string: $oathProtocolErrorString 
        Description provided by library: $errorDescription 
    """.trimIndent(),
    cause
)