package com.iguigui.process.dto

data class BaseResponseDto<T>(
        val status: String,
        val message: String,
        val data: T?
)
