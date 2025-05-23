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
                if(text[i + j] == pattern[j]) {
                    j++
                    addLastMatched(1)
                    message = "Соответствие символов"
                    state = BFState.MATCH
                } else {
                    message = "Несоответствие символов"
                    state = BFState.MISMATCH
                }
                numComparisons++
            }
            BFState.MATCH -> {
                if(j == m) {
                    finished = true
                    compIndex = null
                    message = "Образец найден, начало на индексе $i"
                    return
                }
                message = "Проверка символа правее"
                compIndex = compIndex!! + 1
                state = BFState.COMPARING
            }
            BFState.MISMATCH -> {
                clearMatch()
                i++
                j = 0
                if(i <= n - m) {
                    message = "Сдвиг образца на 1 символ вправо"
                    shiftPattern(1)
                    compIndex = 0
                    state = BFState.COMPARING
                } else {
                    compIndex = null
                    finished = true
                    message = "Образец не найден"
                }
            }
        }
    }

    override fun identify(algorithm: Algorithm): Boolean {
        return algorithm == Algorithm.BRUTE_FORCE
    }
}