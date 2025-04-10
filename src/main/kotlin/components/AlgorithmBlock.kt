package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import util.AlgorithmViewModel

@Composable
fun AlgorithmBlock(
    vm: AlgorithmViewModel,
    modifier: Modifier = Modifier,
    additionalContent: (@Composable (() -> Unit))? = null,
) {
    if(vm.initialized)
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxSize().padding(start = 8.dp, end = 4.dp)
    ) {
        additionalContent?.let {
            additionalContent()
        }
        Text("Количество сравнений: ${vm.numComparisons}")
        Text(vm.message)
        LazyVerticalGrid(
            columns = GridCells.FixedSize(32.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            itemsIndexed(vm.text.toList()) { idx, ch ->
                StringBox(
                    textChar = ch,
                    patternChar = if(idx >= vm.textIndex && idx <= vm.lastIndex) vm.pattern[idx - vm.textIndex] else null,
                    outlined = vm.compIndex != null && vm.textIndex + vm.compIndex!! == idx,
                    matched = vm.matchedFirst != null && vm.matchedLast != null &&
                            idx - vm.textIndex >= vm.matchedFirst!! && idx - vm.textIndex <= vm.matchedLast!!
                )
            }
        }
    }
}