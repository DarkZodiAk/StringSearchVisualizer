package util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object AppEventBus {
    private val _bus = MutableSharedFlow<AppEvent>()
    val bus = _bus.asSharedFlow()

    suspend fun sendEvent(event: AppEvent) = _bus.emit(event)
}

object ActivationBus {
    private val _bus = MutableStateFlow<Algorithm>(Algorithm.DEFAULT)
    val bus = _bus.asStateFlow()

    suspend fun activate(algorithm: Algorithm) = _bus.emit(algorithm)
}