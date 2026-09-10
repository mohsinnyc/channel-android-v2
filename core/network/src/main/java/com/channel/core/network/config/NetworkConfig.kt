package com.channel.core.network.config

/**
 * Only a QA backend exists right now — there is no production API yet.
 * Swap this to a BuildConfig field (per build type) once one exists.
 */
object NetworkConfig {
    const val BASE_URL = "https://qa-api.channelapp.io/"
}
