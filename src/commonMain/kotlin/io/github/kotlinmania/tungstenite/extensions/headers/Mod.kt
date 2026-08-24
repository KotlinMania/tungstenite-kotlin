// port-lint: source extensions/headers/mod.rs
package io.github.kotlinmania.tungstenite.extensions.headers

/**
 * Reads a comma-delimited raw header into a list using the given parser.
 */
public fun <T> fromCommaDelimited(
    values: Iterable<String>,
    parser: (String) -> T?,
): List<T> = fromDelimited(values, ',', parser)

/**
 * Reads a single-character-delimited raw header into a list using the given parser.
 */
public fun <T> fromDelimited(
    values: Iterable<String>,
    delimiter: Char,
    parser: (String) -> T?,
): List<T> {
    val result = ArrayList<T>()
    for (string in values) {
        var inQuotes = false
        val token = StringBuilder()
        for (c in string) {
            if (inQuotes) {
                if (c == '"') {
                    inQuotes = false
                }
                token.append(c)
            } else {
                if (c == delimiter) {
                    val trimmed = token.toString().trim()
                    if (trimmed.isNotEmpty()) {
                        val parsed = parser(trimmed) ?: throw IllegalArgumentException("Invalid header token: $trimmed")
                        result.add(parsed)
                    }
                    token.clear()
                } else {
                    if (c == '"') {
                        inQuotes = true
                    }
                    token.append(c)
                }
            }
        }
        val trimmed = token.toString().trim()
        if (trimmed.isNotEmpty()) {
            val parsed = parser(trimmed) ?: throw IllegalArgumentException("Invalid header token: $trimmed")
            result.add(parsed)
        }
    }
    return result
}
