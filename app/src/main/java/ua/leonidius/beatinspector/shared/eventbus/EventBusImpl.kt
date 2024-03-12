package ua.leonidius.beatinspector.shared.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

object EventBusImpl: EventBus {

    private val flow = MutableSharedFlow<Event>()

    override fun post(event: Event, scope: CoroutineScope) {
        scope.launch {
           flow.emit(event)
        }
    }

    override fun subscribe(
        eventClass: KClass<out Event>,
        scope: CoroutineScope,
        subscriber: (Event) -> Unit
    ) {
        scope.launch {
            flow
                .filterIsInstance(eventClass)
                .collect(subscriber)
        }
    }

}