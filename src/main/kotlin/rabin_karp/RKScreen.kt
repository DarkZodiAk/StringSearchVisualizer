package rabin_karp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import components.AlgorithmBlock

@Composable
fun RKScreen(vm: RKViewModel) {
    AlgorithmBlock(vm) {
        Column {
            Text("Хеш-сумма текущего окна = ${vm.textHash}")
            Text("Хеш-сумма паттерна = ${vm.patternHash}")
        }
    }
}