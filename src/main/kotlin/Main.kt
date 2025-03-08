import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import components.AlgorithmBlock
import components.AppTextField
import components.TextMedium
import kotlinx.coroutines.launch
import util.Algorithm
import util.ViewModels
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
                        onClick = { viewModel.onAction(MainAction.SwitchAlgorithm(algo)) },
                        enabled = !viewModel.isSearchWorking
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
                Button(
                    onClick = { viewModel.onAction(MainAction.ExecuteSearch()) },
                    enabled = !viewModel.isSearchWorking
                ) {
                    Text("Поиск")
                }
                IconButton(
                    onClick = { viewModel.onAction(
                        if(viewModel.isPlaying) MainAction.Pause() else MainAction.Play()
                    ) },
                    enabled = viewModel.isSearchWorking
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
                Button(
                    onClick = { viewModel.onAction(MainAction.Reset()) },
                    enabled = !viewModel.isPlaying
                ) {
                    Text("Сброс")
                }
            }

            Spacer(Modifier.height(16.dp))

            //Экран самого алгоритма
            when(viewModel.algorithm) {
                Algorithm.BRUTE_FORCE -> {
                    AlgorithmBlock(ViewModels.bfViewModel)
                }
                Algorithm.RABIN_KARP -> {
                    AlgorithmBlock(ViewModels.rkViewModel)
                }
                Algorithm.KMP -> {
                    AlgorithmBlock(ViewModels.kmpViewModel)
                }
                Algorithm.BOYER_MOORE -> {
                    AlgorithmBlock(ViewModels.bmViewModel)
                }
            }
        }
    }
}

fun main() = application {
    val viewModel = MainViewModel()
    Window(onCloseRequest = ::exitApplication) {
        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(800, 450)
            window.title = "String Search Visualizer"
        }

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            LaunchedEffect(Unit) {
                scope.launch {
                    viewModel.error.collect { snackbarHostState.showSnackbar(it) }
                }
            }
            App(viewModel)
        }
    }
}
