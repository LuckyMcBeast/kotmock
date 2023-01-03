package native

import common.FailedVerificationException
import kotlin.reflect.KFunction
import common.KotMock as CommonKotMock
import common.FunctionCall as CommonFunctionCall

@Suppress("UNCHECKED_CAST")
open class KotMock : CommonKotMock {
    override val memberFunctionList : MutableList<CommonFunctionCall> = mutableListOf()

    override fun addFunctionToList(function: KFunction<Any?>) {
        if(memberFunctionList.none { it.function == function }) memberFunctionList.add(FunctionCall(function = function))
    }

    override fun clearFunctionList() = memberFunctionList.clear()

    //TODO: use varargs instead of List<Any>
    override fun <T> countFunctionCall(function: KFunction<Any?>, callArgs: List<Any?>) : T {
        addFunctionToList(function)
        memberFunctionList.findFunction(function)?.let { functionCall ->
            functionCall.times += 1
            if (callArgs.isNotEmpty()) functionCall.args.add(callArgs)
            return functionCall.returnOrThrow() as T
        } ?: throw Exception("Failed to retrieve function")
    }

    //TODO: use varargs instead of List<Any>
    override fun verify(function: KFunction<Any?>?, times: Int, args: List<Any?>) {
        function?.let {
            memberFunctionList.findFunction(it)?.let { functionCall ->
                if (args.isNotEmpty()) {
                    checkThatArgsMatch(args, times, functionCall, function.name)
                    return
                }
                checkThatTimesMatch(times, functionCall, function.name)
            } ?: throw FailedVerificationException("No interactions with ${function.name} were found")
        } ?: checkTotalInteractions(times)
    }

    override fun checkTotalInteractions(times: Int) {
        var actualTimes = 0
        memberFunctionList.forEach { entry ->
            actualTimes += entry.times
        }
        if (times != actualTimes) throw FailedVerificationException("$actualTimes total interactions ${this::class.simpleName} were found, wanted $times")
    }

    override fun checkThatArgsMatch(args: List<Any?>, times: Int, functionCall: CommonFunctionCall, functionName : String) {
        functionCall.wasCalledWith(args, times).let { (match, count) ->
            if(!match) throw FailedVerificationException("$count interactions with $functionName with args of $args found, wanted $times")
        }

    }

    override fun checkThatTimesMatch(times: Int, functionCall: CommonFunctionCall, functionName: String) {
        if (functionCall.times != times) throw FailedVerificationException("${functionCall.times} interactions with $functionName were found, wanted $times")
    }

    override fun whenever(function: KFunction<Any?>) : CommonFunctionCall {
        addFunctionToList(function)
        return memberFunctionList.findFunction(function)?: throw Exception("Failed to retrieve function")
    }

    private fun MutableList<CommonFunctionCall>.findFunction(function: KFunction<Any?>) =
        find { it.function == function }
}
