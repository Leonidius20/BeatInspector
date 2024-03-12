package ua.leonidius.beatinspector.shared.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlin.reflect.KClass

interface EventBus {

    fun post(event: Event, scope: CoroutineScope = MainScope())

    fun subscribe(eventClass: KClass<out Event>,
                  scope: CoroutineScope = MainScope(),
                  subscriber: (Event) -> Unit)

}