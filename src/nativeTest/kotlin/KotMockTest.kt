import common.ReturnTypeException
import native.FunctionCall
import kotlin.test.*
import native.KotMock

class KotMockTest {

    class KotMockImpl : KotMock() {
        fun functionWithoutArgsOrReturn() {
          return countFunctionCall(::functionWithoutArgsOrReturn)
        }
        fun functionWithArgNoReturn(number: Int){
            return countFunctionCall(::functionWithArgNoReturn, number)
        }
        fun functionWithArgsNoReturn(number: Int, string: String, boolean: Boolean){
            return countFunctionCall(::functionWithArgsNoReturn, number, string, boolean)
        }
        fun functionWithoutArgsReturn(): Int {
            return countFunctionCall(::functionWithoutArgsReturn)
        }
        fun functionWithArgsReturn(number: Int, string: String, boolean: Boolean): Int {
            return countFunctionCall(::functionWithArgsReturn, number, string, boolean)
        }
        fun functionWithNullableArgsAndReturn(number: Int?, string: String?, boolean: Boolean?): Int? {
            return countFunctionCall(::functionWithNullableArgsAndReturn, number, string, boolean)
        }
    }


    @Test
    fun `should add function to list`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsOrReturn
        val expected = FunctionCall(function)

        kotMockImpl.addFunctionToList(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
        assertEquals(expected, kotMockImpl.memberFunctionList.find { it.function == function })
    }

    @Test
    fun `should clear function list`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsReturn
        val expected = FunctionCall(function)

        kotMockImpl.addFunctionToList(function)
        assertContains(kotMockImpl.memberFunctionList, expected)
        kotMockImpl.clearFunctionList()

        assertTrue(kotMockImpl.memberFunctionList.isEmpty())
    }

    @Test
    fun `should not add function to list if already exists`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsReturn
        val expected = FunctionCall(function)

        kotMockImpl.addFunctionToList(function)
        kotMockImpl.addFunctionToList(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
        assertTrue(kotMockImpl.memberFunctionList.count { it.function == function } == 1)
    }

    @Test
    fun `should add to function call count`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsOrReturn
        val expected = FunctionCall(function, 1)

        kotMockImpl.countFunctionCall<Unit>(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should add to function call count when run multiple times`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsOrReturn
        val expected = FunctionCall(function, 3)

        kotMockImpl.countFunctionCall<Unit>(function)
        kotMockImpl.countFunctionCall<Unit>(function)
        kotMockImpl.countFunctionCall<Unit>(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should add to function call count with a single arg`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgNoReturn
        val expected = FunctionCall(function,1, mutableListOf(listOf(1)))

        kotMockImpl.countFunctionCall<Unit>(function, 1)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should add to function call count with a multiple args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsNoReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))

        kotMockImpl.countFunctionCall<Unit>(function, 1, "hello", true)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should return stored return value whenever`(){
        val kotMockImpl = KotMockImpl()

        kotMockImpl whenever kotMockImpl::functionWithoutArgsReturn thenReturn 4

        assertEquals(4, kotMockImpl.functionWithoutArgsReturn())
    }

    @Test
    fun `should return throw provided value whenever is throwable`(){
        val kotMockImpl = KotMockImpl()

        with(kotMockImpl) { this whenever ::functionWithoutArgsReturn thenThrow NullPointerException() }

        assertFailsWith<NullPointerException> { kotMockImpl.functionWithoutArgsReturn() }
    }

    @Test
    fun `should throw exception if return type is not unit and whenever is not provided`(){
        val kotMockImpl = KotMockImpl()
        kotMockImpl.addFunctionToList(kotMockImpl::functionWithoutArgsReturn)

        assertFailsWith<ReturnTypeException> { kotMockImpl.functionWithoutArgsReturn() }
    }

    @Test
    fun `should throw exception if return type is not nullable and null is provided`(){
        assertFailsWith<NullPointerException> {
            with(KotMockImpl()) { this whenever ::functionWithoutArgsReturn thenReturn null }
        }
    }

    @Test
    fun `should allow null arguments and returns when nullable`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithNullableArgsAndReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(null, null, null)))
        kotMockImpl.whenever(function).thenReturn(null)
        kotMockImpl.functionWithNullableArgsAndReturn(null, null, null)

        kotMockImpl.verify(function, null, null, null)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should verify that function was called`() {
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(6)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        kotMockImpl.verify(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should verify that function was called a provided number of times`() {
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsOrReturn
        val expected = FunctionCall(function, 3)
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()

        kotMockImpl.verify(function, times = 3)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should throw exception when can't verify that function was called`() {
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn

        assertFails { kotMockImpl.verify(function) }
    }

    @Test
    fun `should throw exception when provided times does not match`() {
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsOrReturn
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()

        assertFails { kotMockImpl.verify(function, 2) }
        assertFails { kotMockImpl.verify(function, 10) }
    }

    @Test
    fun `should verify that function was called with provided args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(3)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        kotMockImpl.verify(function, 1, "hello", true)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should should throw exception when can not verify function was called with provided args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(1)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        assertFails { kotMockImpl.verify(function, 2, "goodbye", false) }

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should should throw exception when has correct times but wrong args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsNoReturn
        val expected = FunctionCall(function, 2, mutableListOf(listOf(1, "hello", true), listOf(1, "hello", true)))
        kotMockImpl.functionWithArgsNoReturn(1, "hello", true)
        kotMockImpl.functionWithArgsNoReturn(1, "hello", true)

        assertFails { kotMockImpl.verify(function, 2, "goodbye", false, times = 2) }

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should should throw exception when has correct args but wrong times`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function,1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(4)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        assertFails { kotMockImpl.verify(function, 1, "hello", true, times = 2) }

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should verify total interactions`(){
        val kotMockImpl = KotMockImpl()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithArgNoReturn(4)

        kotMockImpl.verify(times = 2)
    }

    @Test
    fun `should verify zero interactions`(){
        val kotMockImpl = KotMockImpl()

        kotMockImpl.verify(times = 0)
    }
}