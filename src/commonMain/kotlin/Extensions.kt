package common

import kotlin.reflect.KFunction

fun MutableList<FunctionCall>.findFunction(function: KFunction<Any?>) =
    find { it.function == function }