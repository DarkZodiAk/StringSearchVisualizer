package components

import androidx.compose.foundation.background
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
    patternChar: Char? = null,
    outlined: Boolean,
    matched: Boolean
) {
    Column(
        modifier = Modifier.height(76.dp)
    ) {
        CharBox(textChar.toString(), outlined, matched)
        patternChar?.let {
            Spacer(modifier = Modifier.height(12.dp))
            CharBox(patternChar.toString(), outlined)
        }
    }
}

@Composable
fun CharBox(
    str: String,
    outlined: Boolean = false,
    matched: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .then (
                if(outlined) Modifier.border(2.dp, Color.Red)
                else Modifier.border(1.dp, Color.Black)
            ).background(if(matched) Color.Green else Color.Unspecified)
    ) {
        Text(
            text = str,
            fontSize = 16.sp
        )
    }
}