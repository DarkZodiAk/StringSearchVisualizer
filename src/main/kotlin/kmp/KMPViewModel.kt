package kmp

import util.Algorithm
import util.AlgorithmViewModel

class KMPViewModel: AlgorithmViewModel() {
    private var state = KMPState.START
    private var lps = IntArray(0)
    var n = 0
    var m = 0
    var i = 0
    var j = 0
    var shift = 0


    override fun resetData() {
        state = KMPState.START
        i = 0
        j = 0
    }

    override fun nextStep() {
        when(state) {
            KMPState.START -> {
                n = text.length
                m = pattern.length
                computeLPSArray()
                state = KMPState.COMPARING
                compIndex = 0
            }
            KMPState.COMPARING -> {
                if(i < n) {
                    if (pattern[j] == text[i]) {
                        i++
                        j++
                        addLastMatched(1)
                        message = "Соответствие"
                        state = KMPState.MATCH
                    } else {
                        clearMatch()
                        message = "Несоответствие"
                        state = KMPState.MISMATCH
                    }
                    numComparisons++
                } else finishNotFound()
            }
            KMPState.MATCH -> {
                if (j == m) {
                    compIndex = null
                    finished = true
                    message = "Строка найдена, начало на индексе ${i-j}"
                } else {
                    message = "Проверка символа правее"
                    compIndex = compIndex!! + 1
                    state = KMPState.COMPARING
                }
            }
            KMPState.MISMATCH -> {
                if (j != 0) {
                    shift = j - lps[j - 1]
                    if(lastIndex + shift >= n) {
                        finishNotFound()
                        return
                    }
                    message = "Сдвиг подстроки на $shift"
                    shiftPattern(shift)
                    addLastMatched(lps[j - 1])
                    j = lps[j - 1]
                    compIndex = j
                } else {
                    if(lastIndex + 1 >= n) {
                        finishNotFound()
                        return
                    }
                    message = "Сдвиг подстроки на 1"
                    i++
                    shiftPattern(1)
                }
                state = KMPState.COMPARING
            }
        }
    }

    override fun identify(algorithm: Algorithm): Boolean = algorithm == Algorithm.KMP

    private fun finishNotFound() {
        compIndex = null
        finished = true
        message = "Строка не найдена"
    }

    private fun computeLPSArray() {
        val m = pattern.length
        lps = IntArray(m)
        var length = 0
        var i = 1
        lps[0] = 0

        while (i < m) {
            if (pattern[i] == pattern[length]) {
                length++
                lps[i] = length
                i++
            } else {
                if (length != 0) {
                    length = lps[length - 1]
                } else {
                    lps[i] = 0
                    i++
                }
            }
        }
    }
}
