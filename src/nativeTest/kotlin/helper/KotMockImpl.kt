package helper

import native.KotMock

class KotMockImpl : KotMock() {
    fun functionWithoutArgsOrReturn() {
        return handleFunctionCall(::functionWithoutArgsOrReturn)
    }
    fun functionWithArgNoReturn(number: Int){
        return handleFunctionCall(::functionWithArgNoReturn, number)
    }
    fun functionWithArgsNoReturn(number: Int, string: String, boolean: Boolean){
        return handleFunctionCall(::functionWithArgsNoReturn, number, string, boolean)
    }
    fun functionWithoutArgsReturn(): Int {
        return handleFunctionCall(::functionWithoutArgsReturn)
    }
    fun functionWithArgsReturn(number: Int, string: String, boolean: Boolean): Int {
        return handleFunctionCall(::functionWithArgsReturn, number, string, boolean)
    }
    fun functionWithNullableArgsAndReturn(number: Int?, string: String?, boolean: Boolean?): Int? {
        return handleFunctionCall(::functionWithNullableArgsAndReturn, number, string, boolean)
    }
}