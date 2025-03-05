package util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    //Индекс сравниваемого символа. Нужен для пометки сравниваемых символов в UI
    var compIndex by mutableStateOf<Int?>(null)
        protected set

    var numComparisons by mutableStateOf(0)
        protected set
    var message by mutableStateOf("nothing")
        protected set

    protected var speed = 100L
    protected var finished = false

    protected val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    init {
        AppEventBus.bus.onEach { event ->
            onEvent(event)
        }.launchIn(scope)

        /*snapshotFlow { textIndex }.onEach {
            lastIndex = textIndex + pattern.length - 1
            println("textIndex changed. LastIndex is $lastIndex")
        }.launchIn(scope)*/
    }

    //Функция перехода в следующее состояние вьюмодели. Автомат определяется в наследнике.
    protected abstract fun nextStep()
    protected abstract fun resetData()

    //Функция реакции вьюмодели на определенное событие, отправленное из панели управления
    private fun onEvent(event: AppEvent) {
        when(event) {
            is AppEvent.Init -> {
                text = event.text
                pattern = event.pattern
                speed = event.speed.toLong()
                textIndex = 0
                lastIndex = textIndex + pattern.length - 1
                finished = false
                onEvent(AppEvent.Play())
            }
            is AppEvent.ModifySpeed -> {
                speed = event.speed.toLong()
            }
            is AppEvent.Pause -> {
                job?.cancel()
                job = null
                print("received pause event")
            }
            is AppEvent.Play -> {
                job = scope.launch {
                    while(!finished) {
                        nextStep()
                        delay(speed)
                    }
                }
            }
            is AppEvent.Reset -> {
                job?.cancel()
                job = null
                textIndex = 0
                lastIndex = textIndex + pattern.length - 1
                finished = false
                resetData()
            }
            is AppEvent.SkipToFinish -> {
                scope.launch {
                    while(!finished) {
                        nextStep()
                    }
                }
            }
            is AppEvent.StepForward -> {
                scope.launch { nextStep() }
            }
            else -> Unit
        }
    }
}