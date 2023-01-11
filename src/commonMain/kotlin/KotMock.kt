package common

import kotlin.reflect.KFunction

interface KotMock {
    val memberFunctionList : MutableList<FunctionCall>
    fun addFunctionToList(function: KFunction<Any?>)
    fun clearFunctionList()
    fun <T> countFunctionCall(function: KFunction<Any?>, vararg args: Any? = emptyArray()) : T
    fun verify(function: KFunction<Any?>? = null, vararg args: Any? = emptyArray(), times: Int = 1)
    fun checkTotalInteractions(times: Int)
    fun checkThatArgsMatch(args: List<Any?>, times: Int, functionCall: FunctionCall, functionName : String)
    fun checkThatTimesMatch(times: Int, functionCall: FunctionCall, functionName: String)
    infix fun whenever(function: KFunction<Any?>) : FunctionCall
}