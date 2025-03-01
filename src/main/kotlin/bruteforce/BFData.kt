package bruteforce

data class BFData(
    val state: BFState = BFState.START,
    val i: Int = 0,
    val j: Int = 0,
    val n: Int = 0,
    val m: Int = 0
)

enum class BFState {
    START, COMPARING, MATCH, MISMATCH, END
}