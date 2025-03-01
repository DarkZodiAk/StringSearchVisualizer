object Algos {
    fun bruteForceSearch(text: String, pattern: String): Int {
        val n = text.length
        val m = pattern.length

        if (m == 0) return 0
        if (n < m) return -1

        for (i in 0..(n - m)) {
            var j = 0
            while (j < m && text[i + j] == pattern[j]) {
                j++
            }
            if (j == m) return i // match found at index i
        }
        return -1 // no match found
    }

    /* 2. Rabin-Karp String Search
       Uses a rolling hash to quickly filter out positions where pattern cannot match.
       We use a base (e.g., 256 for ASCII) and a prime modulus to compute hash values.
    */
    fun rabinKarpSearch(text: String, pattern: String): Int {
        val n = text.length
        val m = pattern.length
        if (m == 0) return 0
        if (n < m) return -1

        val base = 256
        val prime = 101  // A small prime number to perform modulo; in practice a larger prime is beneficial

        // Precompute the highest power of base needed (base^(m-1) % prime)
        var h = 1
        for (i in 0 until m - 1) {
            h = (h * base) % prime
        }

        // Calculate the hash value of pattern and first window of text
        var patternHash = 0
        var textHash = 0
        for (i in 0 until m) {
            patternHash = (base * patternHash + pattern[i].code) % prime
            textHash = (base * textHash + text[i].code) % prime
        }

        // Slide the pattern over text one by one
        for (i in 0..(n - m)) {
            // If hash values match, then only check for characters one by one.
            if (patternHash == textHash) {
                var j = 0
                while (j < m && text[i + j] == pattern[j]) {
                    j++
                }
                if (j == m) return i // match found
            }
            // Calculate hash value for next window of text:
            // Remove leading digit, add trailing digit
            if (i < n - m) {
                textHash = (base * (textHash - text[i].code * h) + text[i + m].code) % prime
                // We might get negative value of textHash, converting it to positive
                if (textHash < 0) {
                    textHash += prime
                }
            }
        }
        return -1
    }

    /* 3. Knuth–Morris–Pratt (KMP) Search
       This algorithm precomputes a "partial match" table (also called the LPS table)
       that tells us how many characters we can skip from the text when a mismatch occurs.
    */
    fun kmpSearch(text: String, pattern: String): Int {
        val n = text.length
        val m = pattern.length
        if (m == 0) return 0
        if (n < m) return -1

        // Preprocess the pattern to get longest prefix suffix (lps) array
        val lps = computeLPSArray(pattern)

        var i = 0  // index for text
        var j = 0  // index for pattern
        while (i < n) {
            if (pattern[j] == text[i]) {
                i++
                j++
            }
            if (j == m) {
                return i - j // match found; j matches the pattern length
            } else if (i < n && pattern[j] != text[i]) {
                // Mismatch after j matches
                if (j != 0) {
                    j = lps[j - 1]
                } else {
                    i++
                }
            }
        }
        return -1
    }

    fun computeLPSArray(pattern: String): IntArray {
        val m = pattern.length
        val lps = IntArray(m)
        var length = 0 // length of the previous longest prefix suffix
        var i = 1
        lps[0] = 0  // lps[0] is always 0

        while (i < m) {
            if (pattern[i] == pattern[length]) {
                length++
                lps[i] = length
                i++
            } else { // (pattern[i] != pattern[length])
                if (length != 0) {
                    // Also, note that we do not increment i here
                    length = lps[length - 1]
                } else {
                    lps[i] = 0
                    i++
                }
            }
        }
        return lps
    }

    /* 4. Boyer-Moore String Search (using the bad character heuristic) */
    fun boyerMooreSearch(text: String, pattern: String): Int {
        val n = text.length
        val m = pattern.length
        if (m == 0) return 0
        if (n < m) return -1

        // Preprocess: Create the last occurrence table for the pattern characters.
        val lastOccurrence = mutableMapOf<Char, Int>()
        for (i in 0 until m) {
            lastOccurrence[pattern[i]] = i
        }

        var s = 0  // s is the shift of the pattern with respect to text
        while (s <= n - m) {
            var j = m - 1

            // Keep reducing index j while characters of pattern and text are matching at this shift s.
            while (j >= 0 && pattern[j] == text[s + j]) {
                j--
            }
            if (j < 0) {
                // A match is found at shift s
                return s
            } else {
                // Use the bad character rule to determine the shift.
                // Get last occurrence of the mismatched character in pattern
                val lastOccur = lastOccurrence.getOrDefault(text[s + j], -1)
                // Shift pattern so that the mismatched character in text aligns with the last occurrence of it in pattern.
                // If the character is not present in the pattern, lastOccur will be -1 and we shift by j+1.
                s += maxOf(1, j - lastOccur)
            }
        }
        return -1 // no match found
    }
}