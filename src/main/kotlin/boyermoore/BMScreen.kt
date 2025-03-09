package boyermoore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import components.AlgorithmBlock
import components.CharBox

@Composable
fun BMScreen(vm: BMViewModel) {
    AlgorithmBlock(vm) {
        var heuristicsVisible by remember { mutableStateOf(true) }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = if(heuristicsVisible) "Скрыть " else "Показать ",
                    color = Color(26, 140, 255),
                    modifier = Modifier.clickable { heuristicsVisible = !heuristicsVisible }
                )
                Text("Эвристика 'плохих символов'")
            }
            if(heuristicsVisible) {
                Spacer(Modifier.height(4.dp))
                LazyRow {
                    items(vm.lastOccurrence.entries.toList()) {
                        Column {
                            CharBox(it.key.toString())
                            CharBox(it.value.toString())
                        }
                    }
                }
            }
        }
    }
}