import util.Algorithm

sealed interface MainAction {
    class ModifyText(val newText: String): MainAction
    class ModifyPattern(val newPattern: String): MainAction
    class SwitchAlgorithm(val algorithm: Algorithm): MainAction
    class ExecuteSearch: MainAction
    class Play: MainAction
    class Pause: MainAction
    class ModifySpeed(val speed: Float): MainAction
    class Reset: MainAction
    class StepForward: MainAction
    class SkipToFinish: MainAction
}