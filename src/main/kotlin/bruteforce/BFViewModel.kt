package bruteforce

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.AlgorithmViewModel
import util.AppEvent

class BFViewModel: AlgorithmViewModel() {

    private var data = BFData()
    private var job: Job? = null

    override fun onEvent(event: AppEvent) {
        when(event) {
            is AppEvent.Init -> {
                text = event.text
                pattern = event.pattern
                speed = event.speed.toLong()
                textIndex = 0
                lastIndex = textIndex + pattern.length - 1
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
                    while(data.state != BFState.END) {
                        nextStep(speed)
                    }
                }
            }
            is AppEvent.Reset -> {
                job?.cancel()
                job = null
                textIndex = 0
                lastIndex = textIndex + pattern.length - 1
                data = BFData()
            }
            is AppEvent.SkipToFinish -> {
                scope.launch {
                    while(data.state != BFState.END) {
                        nextStep(0L)
                    }
                }
            }
            is AppEvent.StepForward -> {
                scope.launch { nextStep(0L) }
            }
            else -> Unit
        }
    }

    override suspend fun nextStep(delay: Long) {
        when(data.state) {
            BFState.START -> {
                data = BFData(n = text.length, m = pattern.length, state = BFState.COMPARING)
            }
            BFState.COMPARING -> {
                if(data.j < data.m && text[data.i + data.j] == pattern[data.j]) {
                    data = data.copy(state = BFState.MATCH)
                    message = "Соответствие"
                } else if(data.j == data.m) {
                    data = data.copy(state = BFState.END)
                    message = "Строка найдена, начало на индексе ${data.i}"
                } else {
                    data = data.copy(state = BFState.MISMATCH)
                    message = "Несоответствие. Сдвиг строки на 1 символ вправо"
                }
            }
            BFState.MATCH -> {
                data = data.copy(j = data.j + 1, state = BFState.COMPARING)
            }
            BFState.MISMATCH -> {
                data = data.copy(i = data.i + 1, j = data.i + 1)
                if(data.i <= data.n - data.m) {

                    textIndex++
                    lastIndex++

                    data = data.copy(state = BFState.COMPARING)
                } else {
                    data = data.copy(state = BFState.END)
                    message = "Строка не найдена"
                }
            }
            BFState.END -> return
        }

        delay(delay)
    }
}