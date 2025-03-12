package rabin_karp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import util.Algorithm
import util.AlgorithmViewModel

class RKViewModel: AlgorithmViewModel() {
    private var state = RKState.START
    private var n = 0
    private var m = 0
    private val base = 65536L
    private val prime = 1_000_000_007L
    private var h: Long = 1
    var patternHash by mutableStateOf(0L)
    var textHash by mutableStateOf(0L)
    private var i = 0
    private var j = 0


    override fun resetData() {
        state = RKState.START
        h = 1
        patternHash = 0
        textHash = 0
        i = 0
        j = 0
    }

    override fun nextStep() {
        when(state) {
            RKState.START -> {
                n = text.length
                m = pattern.length
                for (k in 0 until m - 1) {
                    h = (h * base) % prime
                }
                for (k in 0 until m) {
                    patternHash = (base * patternHash + pattern[k].code) % prime
                    textHash = (base * textHash + text[k].code) % prime
                }
                state = RKState.COMP_HASH
            }

            RKState.COMP_HASH -> {
                numComparisons++
                if (patternHash == textHash) {
                    state = RKState.COMP_PATTERN
                    message = "Соответствие хеш-сумм. Сравнение строк"
                } else {
                    state = RKState.MISMATCH
                    message = "Несоответствие хеш-сумм"
                }
            }
            RKState.COMP_PATTERN -> {
                compIndex = compIndex ?: 0
                if(j < m && text[i + j] == pattern[j]) {
                    addLastMatched(1)
                    message = "Соответствие"
                    state = RKState.MATCH
                } else {
                    state = RKState.MISMATCH
                    message = "Несоответствие"
                }
                numComparisons++
            }
            RKState.MATCH -> {
                j++
                if(j == m) {
                    finished = true
                    compIndex = null
                    message = "Строка найдена, начало на индексе $i"
                    return
                }
                message = "Проверка символа правее"
                state = RKState.COMP_PATTERN
                compIndex = compIndex!! + 1
            }
            RKState.MISMATCH -> {
                clearMatch()
                compIndex = null
                i++
                j = 0
                if(i <= n - m) {
                    message = "Сдвиг строки на 1 символ вправо"
                    shiftPattern(1)
                    //Пересчет хеш-суммы
                    textHash = (textHash - text[i-1].code.toLong() * h % prime + prime) % prime
                    textHash = (textHash * base + text[i-1 + m].code.toLong()) % prime

                    state = RKState.COMP_HASH
                } else {
                    finished = true
                    message = "Строка не найдена"
                }
            }
        }
    }

    override fun identify(algorithm: Algorithm): Boolean {
        return algorithm == Algorithm.RABIN_KARP
    }
}