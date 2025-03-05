package rabin_karp

enum class RKState {
    START, COMP_HASH, COMP_PATTERN, MATCH, MISMATCH
}