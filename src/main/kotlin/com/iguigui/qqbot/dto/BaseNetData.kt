package com.iguigui.qqbot.dto

data class BaseNetData(
    val magicNumber: Int,
    val messageType: Byte,
    val messageLength:Int)