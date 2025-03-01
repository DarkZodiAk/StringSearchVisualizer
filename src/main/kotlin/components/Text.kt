package components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TextMedium(text: String) {
    Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
}