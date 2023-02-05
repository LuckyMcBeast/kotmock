package native

import common.FailedVerificationException
import common.VerificationType
import common.findFunction
import kotlin.reflect.KFunction
import common.KotMock as CommonKotMock
import common.FunctionCall as CommonFunctionCall
import common.Verification as CommonVerification

@Suppress("UNCHECKED_CAST")
open class KotMock : CommonKotMock {

    override val memberFunctionList: MutableList<CommonFunctionCall> = mutableListOf()
    override var verification: CommonVerification? = null

    override fun addFunctionToList(function: KFunction<Any?>) {
        if (memberFunctionList.none { it.function == function }) memberFunctionList.add(FunctionCall(function = function))
    }

    override fun clearFunctionList() = memberFunctionList.clear()

    override fun <T> handleFunctionCall(function: KFunction<Any?>, vararg args: Any?): T {
        verification?.let { verification ->
            return verifyFunctionCall(verification, function, args.toList()) as T
        } ?: run {
            return countFunctionCall(function, args.toList())
        }
    }

    private fun verifyFunctionCall(
        verification: CommonVerification,
        function: KFunction<Any?>,
        args: List<Any?>
    ) {
        memberFunctionList.findFunction(function)?.let { functionCall ->
            verification.apply {
                this.functionCall = functionCall
                this.args = args
            }.wasCalled()
            this.verification = null
        } ?: throw FailedVerificationException("No interactions with ${function.name} were found")
    }

    private fun <T> countFunctionCall(function: KFunction<Any?>, args: List<Any?>) : T {
        addFunctionToList(function)
        memberFunctionList.findFunction(function)?.let { functionCall ->
            functionCall.times += 1
            if (args.isNotEmpty()) functionCall.args.add(args)
            return functionCall.returnOrThrow() as T
        } ?: throw Exception("Failed to retrieve function")
    }

    override fun whenever(function: KFunction<Any?>): CommonFunctionCall {
        addFunctionToList(function)
        return memberFunctionList.findFunction(function) ?: throw Exception("Failed to retrieve function")
    }

    override fun <T : CommonKotMock> enableVerificationMode(times: Int, type: VerificationType): T {
        this.verification = Verification(kotMock = this, type = type, times = times)
        return this as T
    }

    fun wasCalled() {
        verification?.let { verification ->
            verification.wasCalled()
            this.verification = null
            return
        }
    }
}
