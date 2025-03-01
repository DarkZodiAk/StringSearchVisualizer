import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bruteforce.BFViewModel
import bruteforce.BruteForceScreen
import components.AppTextField
import components.StringBox
import components.TextMedium
import util.Algorithm
import java.awt.Dimension

@Composable
@Preview
fun App(
    viewModel: MainViewModel
) {
    Row {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 200.dp, max = 300.dp)
                .border(width = 1.dp, color = Color.Gray)
                .padding(12.dp)
        ) {
            TextMedium("Текст")
            Spacer(Modifier.height(8.dp))
            AppTextField(viewModel.text) { viewModel.onAction(MainAction.ModifyText(it)) }

            Spacer(Modifier.height(16.dp))

            TextMedium("Что искать?")
            Spacer(Modifier.height(8.dp))
            AppTextField(viewModel.pattern) { viewModel.onAction(MainAction.ModifyPattern(it)) }

            Spacer(Modifier.height(16.dp))

            TextMedium("Алгоритм поиска")
            Spacer(Modifier.height(8.dp))

            Algorithm.entries.forEach { algo ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = viewModel.algorithm == algo,
                        onClick = { viewModel.onAction(MainAction.SwitchAlgorithm(algo)) }
                    )
                    Text(algo.text)
                }
            }
        }


        Column(
            modifier = Modifier.fillMaxHeight()
            .fillMaxWidth(1f)
            .border(1.dp, Color.Black))
        {
            // Панель управления процессом
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Button(onClick = { viewModel.onAction(MainAction.ExecuteSearch()) }) {
                    Text("Поиск")
                }
                IconButton(
                    onClick = { viewModel.onAction(
                        if(viewModel.isPlaying) MainAction.Pause() else MainAction.Play()
                    ) }
                ) {
                    Icon(
                        imageVector = if(viewModel.isPlaying) Icons.Default.Pause
                                        else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }
                Slider(
                    value = viewModel.speed,
                    onValueChange = { viewModel.onAction(MainAction.ModifySpeed(it)) },
                    steps = 50,
                    valueRange = 100f..2000f,
                    modifier = Modifier.width(200.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            //Экран самого алгоритма
            when(viewModel.algorithm) {
                Algorithm.BRUTE_FORCE -> { BruteForceScreen(BFViewModel()) }
                Algorithm.RABIN_KARP -> { }
                Algorithm.KMP -> { }
                Algorithm.BOYER_MOORE -> { }
            }

            /*LazyVerticalGrid(
                columns = GridCells.FixedSize(32.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                items(viewModel.text.toList()) { ch ->
                    StringBox(ch, 'a')
                }
            }*/
        }
    }
}

fun main() = application {
    val viewModel = MainViewModel()
    Window(onCloseRequest = ::exitApplication) {
        LaunchedEffect(true) {
            window.minimumSize = Dimension(800, 450)
            window.title = "String Search Visualizer"
        }
        App(viewModel)
    }
}
