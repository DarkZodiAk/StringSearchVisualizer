package boyermoore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import util.Algorithm
import util.AlgorithmViewModel

class BMViewModel: AlgorithmViewModel() {
    private var state = BMState.START
    val lastOccurrence = mutableStateMapOf<Char, Int>()
    var charInLast by mutableStateOf<Char?>(null)
    private var lastOccur = -1
    private var n = 0
    private var m = 0
    private var j = 0
    private var s = 0


    override fun resetData() {
        state = BMState.START
        charInLast = null
        lastOccurrence.clear()
        lastOccur = 0
        j = 0
        s = 0
    }

    override fun nextStep() {
        when(state) {
            BMState.START -> {
                n = text.length
                m = pattern.length
                for (k in 0 until m) {
                    lastOccurrence[pattern[k]] = k
                }
                state = BMState.SET_INDEX
            }
            BMState.SET_INDEX -> {
                charInLast = null
                if(s <= n - m) {
                    j = m - 1
                    compIndex = m - 1
                    state = BMState.COMPARING
                } else {
                    compIndex = null
                    finished = true
                    message = "Строка не найдена"
                }
            }
            BMState.COMPARING -> {
                charInLast = null
                if(j >= 0 && pattern[j] == text[s + j]) {
                    addFirstMatched(1)
                    j--
                    message = "Соответствие"
                    state = BMState.MATCH
                } else {
                    clearMatch()
                    lastOccur = lastOccurrence.getOrDefault(text[s + j], -1)
                    charInLast = if(lastOccur != -1) text[s + j] else null
                    message = "Несоответствие"
                    state = BMState.MISMATCH
                }
                numComparisons++
            }
            BMState.MATCH -> {
                if(j < 0) {
                    compIndex = null
                    finished = true
                    message = "Строка найдена, начало на индексе $s"
                } else {
                    message = "Проверка символа левее"
                    compIndex = compIndex!! - 1
                    state = BMState.COMPARING
                }
            }
            BMState.MISMATCH -> {
                val shift = maxOf(1, j - lastOccur)
                if(lastIndex + shift >= n) {
                    compIndex = null
                    finished = true
                    message = "Строка не найдена"
                    return
                }
                message = "Сдвиг подстроки на max(1, $j-$lastOccur) = $shift вправо"
                s += shift
                shiftPattern(shift)
                state = BMState.SET_INDEX
            }
        }
    }

    override fun identify(algorithm: Algorithm): Boolean = algorithm == Algorithm.BOYER_MOORE
}