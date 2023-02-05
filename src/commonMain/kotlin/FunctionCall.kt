package common

import kotlin.reflect.KFunction
import kotlin.reflect.KType

interface FunctionCall {
    val function: KFunction<Any?>
    var times: Int
    val args: MutableList<List<Any?>>
    val returnType: KType
    val answers : MutableList<Answer>
    infix fun thenReturn(value: Any?)
    infix fun thenThrow(value: Throwable)
    fun returnOrThrow(): Any?
    fun wasCalledWith(providedArgs: List<Any?>): Int
    fun functionName(): String
}