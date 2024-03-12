package ua.leonidius.beatinspector.shared.logic.eventbus

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

    override fun <E : Event> subscribe(
        eventClass: KClass<E>,
        scope: CoroutineScope,
        subscriber: suspend (E) -> Unit
    ) {
        scope.launch {
            flow
                .filterIsInstance(eventClass)
                .collect(subscriber)
        }
    }

}