package bruteforce

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import util.AlgorithmViewModel

class BFViewModel: AlgorithmViewModel() {

    var state by mutableStateOf(BFState.START)
    var i by mutableStateOf(0)
    var j by mutableStateOf(0)
    var n by mutableStateOf(0)
    var m by mutableStateOf(0)

    override fun resetData() {
        state = BFState.START
        i = 0
        j = 0
        m = 0
        n = 0
    }

    override fun nextStep() {
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
                    compIndex = null
                    finished = true
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
                    compIndex = null
                    finished = true
                    message = "Строка не найдена"
                }
            }
        }
    }
}