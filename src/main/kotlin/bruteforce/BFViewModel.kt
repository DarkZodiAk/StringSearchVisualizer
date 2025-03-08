package bruteforce

import util.Algorithm
import util.AlgorithmViewModel

class BFViewModel: AlgorithmViewModel() {

    private var state = BFState.START
    private var i = 0
    private var j = 0
    private var n = 0
    private var m = 0

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
                    numComparisons++
                    state = BFState.MATCH
                    message = "Соответствие, проверка символа правее"
                } else if(j == m) {
                    compIndex = null
                    finished = true
                    message = "Строка найдена, начало на индексе $i"
                } else {
                    numComparisons++
                    state = BFState.MISMATCH
                    message = "Несоответствие. Сдвиг строки на 1 символ вправо"
                }
            }
            BFState.MATCH -> {
                j++
                addLastMatched(1)
                state = BFState.COMPARING
                compIndex = compIndex!! + 1
            }
            BFState.MISMATCH -> {
                clearMatch()
                i++
                j = 0
                if(i <= n - m) {
                    shiftPattern(1)
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

    override fun identify(algorithm: Algorithm): Boolean = algorithm == Algorithm.BRUTE_FORCE
}