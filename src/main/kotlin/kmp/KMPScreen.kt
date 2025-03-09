package kmp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
fun KMPScreen(vm: KMPViewModel) {
    AlgorithmBlock(vm) {
        var lpsVisible by remember { mutableStateOf(true) }
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = if(lpsVisible) "Скрыть " else "Показать ",
                    color = Color(26, 140, 255),
                    modifier = Modifier.clickable { lpsVisible = !lpsVisible }
                )
                Text("LPS-массив")
            }
            if(lpsVisible){
                Spacer(Modifier.height(4.dp))
                LazyRow {
                    itemsIndexed(vm.lpsList) { idx, prefix ->
                        Column {
                            CharBox(idx.toString())
                            CharBox(vm.pattern[idx].toString())
                            CharBox(prefix.toString())
                        }
                    }
                }
            }
        }
    }
}