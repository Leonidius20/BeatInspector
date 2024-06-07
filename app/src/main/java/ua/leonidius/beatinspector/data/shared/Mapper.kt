package ua.leonidius.beatinspector.data.shared

import androidx.annotation.Keep

@Keep
interface Mapper<T> {

    fun toDomainObject(): T

}