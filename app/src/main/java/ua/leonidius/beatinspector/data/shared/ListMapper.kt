package ua.leonidius.beatinspector.data.shared

import androidx.annotation.Keep

@Keep
interface ListMapper<T> {

    fun toDomainObject(): List<T>

}