package kmp

import util.AlgorithmViewModel

class KMPViewModel: AlgorithmViewModel() {
    private var state = KMPState.START
    private var lps = IntArray(0)
    var n = 0
    var m = 0
    var i = 0
    var j = 0


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
                    numComparisons++
                    if (pattern[j] == text[i]) {
                        i++
                        j++
                        addLastMatched(1)
                        if (j == m) {
                            compIndex = null
                            finished = true
                            message = "Строка найдена, начало на индексе ${i-j}"
                        } else {
                            message = "Соответствие, проверка символа правее"
                            compIndex = compIndex!! + 1
                        }
                    } else {
                        clearMatch()
                        if (j != 0) {
                            val shift = j - lps[j - 1]
                            message = "Несоответствие. Сдвиг подстроки на $shift"
                            shiftPattern(shift)
                            addLastMatched(lps[j - 1])
                            j = lps[j - 1]
                            compIndex = j
                        } else {
                            message = "Несоответствие. Сдвиг подстроки на 1"
                            i++
                            shiftPattern(1)
                        }
                    }
                } else {
                    compIndex = null
                    finished = true
                    message = "Строка не найдена"
                }
            }
            KMPState.MATCH -> {

            }
            KMPState.MISMATCH -> {

            }
        }
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
