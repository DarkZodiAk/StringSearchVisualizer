package util

sealed interface AppEvent {
    class Init(val text: String, val pattern: String, val speed: Float): AppEvent
    class Play: AppEvent
    class Pause: AppEvent
    class Reset: AppEvent
    class ModifySpeed(val speed: Float): AppEvent
    class StepForward: AppEvent
    class SkipToFinish: AppEvent
    class Finish: AppEvent
}