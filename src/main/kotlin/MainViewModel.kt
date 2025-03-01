import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import util.Algorithm
import util.AppEvent
import util.AppEventBus

class MainViewModel {
    var text by mutableStateOf("")
        private set
    var pattern by mutableStateOf("")
        private set
    var algorithm by mutableStateOf(Algorithm.BRUTE_FORCE)
        private set

    var speed by mutableFloatStateOf(100f)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var isSearchWorking by mutableStateOf(false)
        private set

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        AppEventBus.bus.onEach { event ->
            if(event is AppEvent.Finish) {
                isPlaying = false
                isSearchWorking = false
            }
        }.launchIn(scope)
    }

    fun onAction(action: MainAction) {
        when(action) {
            is MainAction.ModifyPattern -> { pattern = action.newPattern }
            is MainAction.ModifyText -> { text = action.newText }
            is MainAction.SwitchAlgorithm -> { algorithm = action.algorithm }
            is MainAction.ExecuteSearch -> {
                isSearchWorking = true
                isPlaying = true
                scope.launch { AppEventBus.sendEvent(AppEvent.Init(text, pattern, speed)) }
            }
            is MainAction.ModifySpeed -> {
                speed = action.speed
                scope.launch { AppEventBus.sendEvent(AppEvent.ModifySpeed(action.speed)) }
            }
            is MainAction.Pause -> {
                isPlaying = false
                scope.launch { AppEventBus.sendEvent(AppEvent.Pause()) }
            }
            is MainAction.Play -> {
                isPlaying = true
                scope.launch { AppEventBus.sendEvent(AppEvent.Play()) }
            }
            is MainAction.SkipToFinish -> {
                scope.launch { AppEventBus.sendEvent(AppEvent.SkipToFinish()) }
                isPlaying = false
            }
            is MainAction.StepForward -> {
                scope.launch { AppEventBus.sendEvent(AppEvent.StepForward()) }
            }
            is MainAction.Reset -> {

            }
        }
    }

    private fun stepForward() {

    }

    private fun skipToFinish() {

    }
}