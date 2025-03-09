package util
enum class Algorithm(val text: String) {
    BRUTE_FORCE("Метод перебора"),
    RABIN_KARP("Алгоритм Рабина-Карпа"),
    KMP("Алгортим Кнута-Морриса-Пратта"),
    BOYER_MOORE("Алгоритм Бойер-Мура");

    companion object {
        val DEFAULT = BRUTE_FORCE
    }
}