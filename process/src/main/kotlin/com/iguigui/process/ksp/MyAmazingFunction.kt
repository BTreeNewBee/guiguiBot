package com.iguigui.process.ksp

import com.iguigui.common.kotlin.MyFunction


@MyFunction( name = "myAmazingFunction")
interface MyAmazingFunction {
    val arg1: String
    val arg2: Map<String, List<*>>
}