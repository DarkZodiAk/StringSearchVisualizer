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
import rabin_karp.RKScreen
import boyermoore.BMScreen
import kmp.KMPScreen
import components.AlgorithmBlock
import components.AppButton
import components.AppTextField
import components.TextMedium
import kotlinx.coroutines.launch
import util.Algorithm
import util.ViewModels
import java.awt.Dimension

@Composable
fun App(viewModel: MainViewModel) {
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

            TextMedium("Образец для поиска")
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
                        enabled = !viewModel.isSearchWorking,
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0, 102, 204))
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
            //Верхняя панель управления
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                AppButton(
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
                AppButton(
                    onClick = { viewModel.onAction(MainAction.Reset()) },
                    enabled = !viewModel.isPlaying
                ) {
                    Text("Сброс")
                }
            }

            //Экран алгоритма
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

            //Нижняя панель управления
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
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0, 102, 204),
                            activeTrackColor = Color(51, 153, 255),
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        ),
                        modifier = Modifier.width(200.dp)
                    )
                    Text(
                        text = "Скорость анимации",
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }
                AppButton(
                    onClick = { viewModel.onAction(MainAction.StepForward()) },
                    enabled = viewModel.isSearchWorking && !viewModel.isPlaying
                ) {
                    Text("Шаг вперед")
                }
                AppButton(
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
            window.title = "Визуализация поиска строк"
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