package util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppEventBus {
    private val _bus = MutableSharedFlow<AppEvent>()
    val bus = _bus.asSharedFlow()

    suspend fun sendEvent(event: AppEvent) = _bus.emit(event)
}