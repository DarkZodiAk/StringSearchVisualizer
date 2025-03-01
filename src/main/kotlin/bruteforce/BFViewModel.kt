package bruteforce

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.AlgorithmViewModel
import util.AppEvent

class BFViewModel: AlgorithmViewModel() {

    //private var data = BFData()
    private var job: Job? = null

    var state by mutableStateOf(BFState.START)
    var i by mutableStateOf(0)
    var j by mutableStateOf(0)
    var n by mutableStateOf(0)
    var m by mutableStateOf(0)

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
                print("received pause event")
            }
            is AppEvent.Play -> {
                job = scope.launch {
                    while(state != BFState.END) {
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
                resetData()
            }
            is AppEvent.SkipToFinish -> {
                scope.launch {
                    while(state != BFState.END) {
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

    private fun resetData() {
        state = BFState.START
        i = 0
        j = 0
        m = 0
        n = 0
    }

    override suspend fun nextStep() {
        when(state) {
            BFState.START -> {
                n = text.length
                m = pattern.length
                state = BFState.COMPARING
                compIndex = 0
            }
            BFState.COMPARING -> {
                if(j < m && text[i + j] == pattern[j]) {
                    state = BFState.MATCH
                    message = "Соответствие"
                } else if(j == m) {
                    state = BFState.END
                    message = "Строка найдена, начало на индексе $i"
                } else {
                    state = BFState.MISMATCH
                    message = "Несоответствие. Сдвиг строки на 1 символ вправо"
                }
            }
            BFState.MATCH -> {
                j++
                state = BFState.COMPARING
                compIndex = compIndex!! + 1

            }
            BFState.MISMATCH -> {
                i++
                j = 0
                if(i <= n - m) {

                    textIndex++
                    lastIndex++

                    state = BFState.COMPARING
                    compIndex = 0
                } else {
                    state = BFState.END
                    message = "Строка не найдена"
                }
            }
            BFState.END -> return
        }
    }
}