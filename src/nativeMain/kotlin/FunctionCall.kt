package native

import common.FunctionCall as CommonFunctionCall
import common.Answer as CommonAnswer
import common.ReturnTypeException
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class FunctionCall(
    override val function: KFunction<Any?>,
    override var times: Int = 0,
    override val args: MutableList<List<Any?>> = mutableListOf(),
) : CommonFunctionCall {
    override val answers: MutableList<CommonAnswer> = mutableListOf()
    override val returnType: KType = function.returnType

    override infix fun thenReturn(value: Any?) {
        value?.let {
            addIfTypesafe(it)
            return
        } ?: addIfTypeIsNullable(value)
    }

    private fun addIfTypeIsNullable(value: Any?) {
        if (returnType.isMarkedNullable) answers.add(Answer(value)) else throw NullPointerException("Provided returnType of $returnType is not nullable")
    }

    private fun addIfTypesafe(it: Any) {
        if (returnType.classifier == it::class) {
            answers.add(Answer(it))
            return
        }
        throw ReturnTypeException("Provided returnType of ${returnType.classifier} does not match value of $it. $it is of type ${it::class.simpleName}")
    }

    override infix fun thenThrow(value: Throwable) {
        answers.add(Answer(value, true))
    }

    //TODO: Make responses more verbose
    override fun returnOrThrow(): Any? {
       return answers.firstOrNull { (value, toBeThrown) ->
           return provideAnswer(toBeThrown, value)
        } ?: returnUnitOrFail()
    }

    private fun provideAnswer(toBeThrown: Boolean, value: Any?): Any? {
        answers.removeFirst()
        when {
            toBeThrown -> {
                println("KotMock: Throwing $value for ${function.name}"); throw value as Throwable
            }

            else -> {
                println("KotMock: Returning $value for ${function.name}"); return value
            }
        }
    }

    private fun returnUnitOrFail() {
        if (returnType.classifier == Unit::class) return else throw ReturnTypeException("Return value is either not of type $returnType, non-existant, or, if it is a Throwable, is not set to be thrown.")
    }

    override fun wasCalledWith(providedArgs: List<Any?>, times: Int): Pair<Boolean, Int> {
        args.count { providedArgs == it }.let { count ->
            return Pair(count == times, count)
        }
    }
}


