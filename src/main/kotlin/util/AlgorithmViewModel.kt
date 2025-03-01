package util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class AlgorithmViewModel {
    var text by mutableStateOf("")
        protected set
    var pattern by mutableStateOf("")
        protected set
    //Индекс символа текста, относительно которого стартует паттерн поиска. В алгоритмах это копия переменной i.
    var textIndex by mutableStateOf(0)
        protected set
    //Индекс последнего символа паттерна (тоже относительно текста). Должен обновляться при изменении textIndex
    var lastIndex by mutableStateOf(0)
        protected set

    var numComparisons by mutableStateOf(0)
        protected set
    var message by mutableStateOf("nothing")
        protected set

    protected var speed = 100L

    protected val scope = CoroutineScope(Dispatchers.Default)

    init {
        AppEventBus.bus.onEach { event ->
            onEvent(event)
        }.launchIn(scope)

        /*snapshotFlow { textIndex }.onEach {
            lastIndex = textIndex + pattern.length - 1
            println("textIndex changed. LastIndex is $lastIndex")
        }.launchIn(scope)*/
    }

    abstract fun onEvent(event: AppEvent)
    abstract suspend fun nextStep(delay: Long)
}