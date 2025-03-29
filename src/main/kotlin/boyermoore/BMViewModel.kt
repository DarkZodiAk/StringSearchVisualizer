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
    private var i = 0


    override fun resetData() {
        state = BMState.START
        charInLast = null
        lastOccurrence.clear()
        lastOccur = 0
        i = 0
    }

    override fun nextStep() {
        when(state) {
            BMState.START -> {
                n = text.length
                m = pattern.length
                j = m - 1
                compIndex = m - 1
                for (k in 0 until m) {
                    lastOccurrence[pattern[k]] = k
                }
                state = BMState.COMPARING
            }
            BMState.COMPARING -> {
                charInLast = null
                if(pattern[j] == text[i + j]) {
                    addFirstMatched(1)
                    j--
                    message = "Соответствие символов"
                    state = BMState.MATCH
                } else {
                    clearMatch()
                    lastOccur = lastOccurrence.getOrDefault(text[i + j], -1)
                    charInLast = if(lastOccur != -1) text[i + j] else null
                    message = "Несоответствие символов"
                    state = BMState.MISMATCH
                }
                numComparisons++
            }
            BMState.MATCH -> {
                if(j < 0) {
                    compIndex = null
                    finished = true
                    message = "Образец найден, начало на индексе $i"
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
                    message = "Образец не найден"
                    return
                }
                message = "Сдвиг образца на max(1, $j-$lastOccur) = $shift вправо"
                i += shift
                shiftPattern(shift)
                j = m - 1
                compIndex = m - 1
                state = BMState.COMPARING
            }
        }
    }

    override fun identify(algorithm: Algorithm): Boolean {
        return algorithm == Algorithm.BOYER_MOORE
    }
}