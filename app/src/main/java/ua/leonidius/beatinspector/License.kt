package ua.leonidius.beatinspector

import androidx.annotation.StringRes

/**
 * Represents a software license for an open-source library used
 * in the app.
 */
sealed class License(@StringRes val textId: Int) {

    // object Apache20 : License(R.string.license_apache_20)

    class Custom(@StringRes textId: Int) : License(textId)

}

// todo: mapping between a list of used libs and their licenses