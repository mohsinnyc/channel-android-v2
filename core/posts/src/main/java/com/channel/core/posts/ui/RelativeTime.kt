package com.channel.core.posts.ui

import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

/** "2h", "1d", "3w" — coarse, Twitter-style relative timestamps. Returns null for an unparsable/missing value. */
fun relativeTimeLabel(iso8601: String?, now: Instant = Instant.now()): String? {
    if (iso8601.isNullOrBlank()) return null
    val then = try {
        Instant.parse(iso8601)
    } catch (e: DateTimeParseException) {
        return null
    }
    val minutes = Duration.between(then, now).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        minutes < 60 * 24 -> "${minutes / 60}h"
        minutes < 60 * 24 * 7 -> "${minutes / (60 * 24)}d"
        else -> "${minutes / (60 * 24 * 7)}w"
    }
}
