package boyermoore

import util.Algorithm
import util.AlgorithmViewModel

class BMViewModel: AlgorithmViewModel() {
    private var state = BMState.START
    val lastOccurrence = mutableMapOf<Char, Int>()
    var lastOccur = 0
    var n = 0
    var m = 0
    var j = 0
    var s = 0


    override fun resetData() {
        state = BMState.START
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
                if(j >= 0 && pattern[j] == text[s + j]) {
                    addFirstMatched(1)
                    j--
                    message = "Соответствие"
                    state = BMState.MATCH
                } else {
                    clearMatch()
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
                lastOccur = lastOccurrence.getOrDefault(text[s + j], -1)
                val shift = maxOf(1, j - lastOccur)
                if(lastIndex + shift >= n) {
                    compIndex = null
                    finished = true
                    message = "Строка не найдена"
                    return
                }
                message = "Сдвиг подстроки на $shift вправо"
                s += shift
                shiftPattern(shift)
                state = BMState.SET_INDEX
            }
        }
    }

    override fun identify(algorithm: Algorithm): Boolean = algorithm == Algorithm.BOYER_MOORE
}