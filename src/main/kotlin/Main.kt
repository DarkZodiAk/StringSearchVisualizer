import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import boyermoore.BMScreen
import components.AlgorithmBlock
import components.AppTextField
import components.TextMedium
import kmp.KMPScreen
import kotlinx.coroutines.launch
import rabin_karp.RKScreen
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
                .verticalScroll(rememberScrollState())
                .width(300.dp)
                .border(width = 1.dp, color = Color.Gray)
                .padding(12.dp)
        ) {
            TextMedium("Текст")
            Spacer(Modifier.height(8.dp))
            AppTextField(
                value = viewModel.text,
                onValueChange = { viewModel.onAction(MainAction.ModifyText(it)) },
                modifier = Modifier.heightIn(max = 256.dp).height(IntrinsicSize.Min)
            )

            Spacer(Modifier.height(16.dp))

            TextMedium("Что искать?")
            Spacer(Modifier.height(8.dp))
            AppTextField(
                value = viewModel.pattern,
                onValueChange = { viewModel.onAction(MainAction.ModifyPattern(it)) },
                modifier = Modifier.heightIn(max = 256.dp).height(IntrinsicSize.Min)
            )

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
                .border(1.dp, Color.Black)
        ) {
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
                Button(
                    onClick = { viewModel.onAction(MainAction.Reset()) },
                    enabled = !viewModel.isPlaying
                ) {
                    Text("Сброс")
                }
            }

            //Экран самого алгоритма
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when(viewModel.algorithm) {
                    Algorithm.BRUTE_FORCE -> {
                        AlgorithmBlock(ViewModels.bfViewModel)
                    }
                    Algorithm.RABIN_KARP -> {
                        RKScreen(ViewModels.rkViewModel)
                    }
                    Algorithm.KMP -> {
                        KMPScreen(ViewModels.kmpViewModel)
                    }
                    Algorithm.BOYER_MOORE -> {
                        BMScreen(ViewModels.bmViewModel)
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Slider(
                        value = viewModel.speed,
                        onValueChange = { viewModel.onAction(MainAction.ModifySpeed(it)) },
                        steps = 50,
                        valueRange = 50f..2000f,
                        modifier = Modifier.width(200.dp)
                    )
                    Text(
                        text = "Скорость анимации",
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }
                Button(
                    onClick = { viewModel.onAction(MainAction.StepForward()) },
                    enabled = viewModel.isSearchWorking && !viewModel.isPlaying
                ) {
                    Text("Шаг вперед")
                }
                Button(
                    onClick = { viewModel.onAction(MainAction.SkipToFinish()) },
                    enabled = viewModel.isSearchWorking && !viewModel.isPlaying
                ) {
                    Text("В конец")
                }
            }
        }
    }
}

fun main() = application {
    val viewModel = MainViewModel()
    Window(onCloseRequest = ::exitApplication) {
        LaunchedEffect(Unit) {
            window.minimumSize = Dimension(780, 580)
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
