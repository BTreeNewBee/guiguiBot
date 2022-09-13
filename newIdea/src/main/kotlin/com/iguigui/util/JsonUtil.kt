package com.iguigui.util

import kotlinx.serialization.json.Json

val Json by lazy {
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}
