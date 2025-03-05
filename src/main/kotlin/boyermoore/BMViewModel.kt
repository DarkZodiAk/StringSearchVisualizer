package boyermoore

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
                    numComparisons++
                    addFirstMatched(1)
                    j--
                    compIndex = compIndex!! - 1
                } else {
                    if(j < 0) {
                        compIndex = null
                        finished = true
                        message = "Строка найдена, начало на индексе $s"
                    } else {
                        clearMatch()
                        numComparisons++
                        lastOccur = lastOccurrence.getOrDefault(text[s + j], -1)
                        s += maxOf(1, j - lastOccur)
                        textIndex += maxOf(1, j - lastOccur)
                        lastIndex += maxOf(1, j - lastOccur)
                        state = BMState.SET_INDEX
                    }
                }
            }
        }
    }
}