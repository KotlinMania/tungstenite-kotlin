// port-lint: source util.rs
package io.github.kotlinmania.tungstenite

/**
 * Non-blocking IO handling.
 */
public interface NonBlockingError {
    /**
     * Convert WouldBlock to null and don't touch other errors.
     */
    public fun intoNonBlocking(): Throwable?
}

/**
 * Non-blocking IO wrapper.
 *
 * Perform the non-block conversion on a [Result]. If the failure error represents
 * a non-blocking WouldBlock condition, it is converted to `Result.success(null)`.
 */
public fun <T> Result<T>.noBlock(): Result<T?> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { error ->
            if (error is NonBlockingError) {
                val nonBlocking = error.intoNonBlocking()
                if (nonBlocking == null) {
                    Result.success(null)
                } else {
                    Result.failure(nonBlocking)
                }
            } else if (error is TungsteniteException.Io && error.message?.contains("WouldBlock", ignoreCase = true) == true) {
                Result.success(null)
            } else {
                Result.failure(error)
            }
        },
    )
