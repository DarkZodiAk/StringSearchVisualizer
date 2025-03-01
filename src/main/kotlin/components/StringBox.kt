package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StringBox(
    textChar: Char,
    patternChar: Char? = null
) {
    Column(
        modifier = Modifier.height(76.dp)
    ) {
        CharBox(textChar)
        patternChar?.let {
            Spacer(modifier = Modifier.height(12.dp))
            CharBox(patternChar)
        }
    }
}

@Composable
fun CharBox(char: Char) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .border(1.dp, Color.Black)
    ) {
        Text(
            text = char.toString(),
            fontSize = 16.sp
        )
    }
}