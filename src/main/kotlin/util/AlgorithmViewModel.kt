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
    private var activated = false

    var initialized by mutableStateOf(false)
        private set

    var text by mutableStateOf("")
        private set
    var pattern by mutableStateOf("")
        private set

    //Индекс символа текста, относительно которого стартует паттерн поиска. В алгоритмах это копия переменной i.
    var textIndex by mutableStateOf(0)
        private set
    //Индекс последнего символа паттерна (тоже относительно текста).
    var lastIndex by mutableStateOf(0)
        private set
    //Индекс сравниваемого символа относительно паттерна. Нужен для пометки сравниваемого символа в UI
    var compIndex by mutableStateOf<Int?>(null)
        protected set
    //Индекс первого (левого) символа среди сопоставленных относительно паттерна
    var matchedFirst by mutableStateOf<Int?>(null)
        private set
    //Индекс последнего (правого) символа среди сопоставленных относительно паттерна
    var matchedLast by mutableStateOf<Int?>(null)
        private set


    var numComparisons by mutableStateOf(0)
        protected set
    var message by mutableStateOf("")
        protected set

    private var speed = 100L
    protected var finished = false

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    init {
        ActivationBus.bus.onEach { algo ->
            activated = identify(algo)
        }.launchIn(scope)

        AppEventBus.bus.onEach { event ->
            if(activated) onEvent(event)
        }.launchIn(scope)
    }

    protected abstract fun nextStep()
    protected abstract fun resetData()
    protected abstract fun identify(algorithm: Algorithm): Boolean


    //Группа функций для управления отображением сопоставленных символов в UI
    protected fun addFirstMatched(offset: Int) {
        if(offset < 0 || offset > pattern.length)
            throw IllegalArgumentException("Offset is out of range [0, ${pattern.length}]")
        if(matchedFirst == null) {
            matchedFirst = pattern.length - offset
            matchedLast = pattern.length - 1
        } else {
            matchedFirst = matchedFirst!! - offset
        }
    }
    protected fun addLastMatched(offset: Int) {
        if(offset < 0 || offset > pattern.length)
            throw IllegalArgumentException("Offset is out of range [0, ${pattern.length}]")
        if(matchedFirst == null) {
            matchedFirst = 0
            matchedLast = offset - 1
        } else {
            matchedLast = matchedLast!! + offset
        }
    }
    protected fun matchAll() {
        matchedFirst = 0
        matchedLast = pattern.length - 1
    }
    protected fun clearMatch() {
        matchedFirst = null
        matchedLast = null
    }

    //Функция сдвига паттерна относительно текста
    protected fun shiftPattern(offset: Int) {
        if(textIndex + offset < 0 || lastIndex + offset > text.length)
            throw IllegalArgumentException("Tried to shift pattern out of text range")
        textIndex += offset
        lastIndex += offset
    }

    //Функция реакции вьюмодели на определенное событие, отправленное из панели управления
    private fun onEvent(event: AppEvent) {
        when(event) {
            is AppEvent.Init -> {
                if(finished) onEvent(AppEvent.Reset())
                message = ""
                text = event.text
                pattern = event.pattern
                speed = event.speed.toLong()
                textIndex = 0
                lastIndex = textIndex + pattern.length - 1
                finished = false
                initialized = true
                onEvent(AppEvent.Play())
            }
            is AppEvent.ModifySpeed -> {
                speed = event.speed.toLong()
            }
            is AppEvent.Pause -> {
                job?.cancel()
                job = null
            }
            is AppEvent.Play -> {
                job = scope.launch {
                    while(!finished) {
                        nextStep()
                        delay(speed)
                    }
                    sendFinish()
                }
            }
            is AppEvent.Reset -> {
                if(!initialized) {
                    scope.launch { AppEventBus.sendEvent(AppEvent.AlreadyResetted()) }
                    return
                }
                job?.cancel()
                job = null
                initialized = false

                textIndex = 0
                lastIndex = textIndex + pattern.length - 1
                finished = false
                clearMatch()
                numComparisons = 0
                compIndex = null
                resetData()
            }
            is AppEvent.SkipToFinish -> {
                scope.launch {
                    while(!finished) {
                        nextStep()
                    }
                    sendFinish()
                }
            }
            is AppEvent.StepForward -> {
                scope.launch { nextStep() }
                if(finished) sendFinish()
            }
            else -> Unit
        }
    }

    private fun sendFinish() {
        scope.launch {
            AppEventBus.sendEvent(AppEvent.Finish())
        }
    }
}