package common

import kotlin.reflect.KFunction

interface KotMock {

    val memberFunctionList : MutableList<FunctionCall>
    fun addFunctionToList(function: KFunction<Any?>)
    fun clearFunctionList()
    fun <T> handleFunctionCall(function: KFunction<Any?>, vararg args: Any? = emptyArray()) : T
    infix fun whenever(function: KFunction<Any?>) : FunctionCall
    var verification : Verification?
    fun <T : KotMock> enableVerificationMode(times: Int, type: VerificationType): T
}