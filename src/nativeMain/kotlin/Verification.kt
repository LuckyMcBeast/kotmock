package native

import common.FailedVerificationException
import common.FunctionCall
import common.KotMock
import common.VerificationType
import common.Verification as CommonVerification

data class Verification(
    override val kotMock: KotMock,
    override var functionCall: FunctionCall? = null,
    override var args: List<Any?> = emptyList(),
    override val type: VerificationType,
    override val times: Int
): CommonVerification {

    override fun wasCalled() {
        functionCall?.let { functionCall ->
            if (args.isNotEmpty()) {
                checkThatArgsMatch(functionCall, args.toList(), times)
                return
            }
            checkThatTimesMatch(times, functionCall)
        } ?: checkTotalInteractions(times)
    }

    private fun checkThatArgsMatch(functionCall: FunctionCall, args: List<Any?>, times: Int) {
        functionCall.wasCalledWith(args).let { count ->
            if(!type.evaluate(count, times)) throw FailedVerificationException("$count interactions with ${functionCall.functionName()} with args of $args found, wanted $times")
        }
    }

    private fun checkThatTimesMatch(times: Int, functionCall: FunctionCall) {
        if (!type.evaluate(functionCall.times, times)) throw FailedVerificationException("${functionCall.times} interactions with ${functionCall.functionName()} were found, wanted $times")
    }

    private fun checkTotalInteractions(times: Int) {
        var actualTimes = 0
        kotMock.memberFunctionList.forEach { entry ->
            actualTimes += entry.times
        }
        if (times != actualTimes) throw FailedVerificationException("$actualTimes total interactions ${kotMock::class.simpleName} were found, wanted $times")
    }
}