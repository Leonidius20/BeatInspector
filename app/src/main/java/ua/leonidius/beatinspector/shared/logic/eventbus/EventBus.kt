package ua.leonidius.beatinspector.shared.logic.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlin.reflect.KClass

interface EventBus {

    fun post(event: Event, scope: CoroutineScope = MainScope())

    fun <E: Event> subscribe(eventClass: KClass<E>,
                  scope: CoroutineScope = MainScope(),
                  subscriber: suspend (E) -> Unit)

}