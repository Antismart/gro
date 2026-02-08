package com.example.gro.util

import android.util.Log
import kotlinx.coroutines.delay

suspend fun <T> withRetry(
    maxRetries: Int = 3,
    initialDelayMs: Long = 500,
    maxDelayMs: Long = 5000,
    tag: String = "Retry",
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMs
    var lastException: Exception? = null

    repeat(maxRetries) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            Log.w(tag, "Attempt ${attempt + 1}/$maxRetries failed: ${e.message}")
            if (attempt < maxRetries - 1) {
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMs)
            }
        }
    }

    throw lastException ?: IllegalStateException("Retry failed")
}
