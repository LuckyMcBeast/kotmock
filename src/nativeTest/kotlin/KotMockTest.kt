import common.ReturnTypeException
import helper.KotMockImpl
import native.FunctionCall
import native.WithKotMock
import kotlin.test.*

class KotMockTest : WithKotMock() {

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

        kotMockImpl.handleFunctionCall<Unit>(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should add to function call count when run multiple times`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithoutArgsOrReturn
        val expected = FunctionCall(function, 3)

        kotMockImpl.handleFunctionCall<Unit>(function)
        kotMockImpl.handleFunctionCall<Unit>(function)
        kotMockImpl.handleFunctionCall<Unit>(function)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should add to function call count with a single arg`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgNoReturn
        val expected = FunctionCall(function,1, mutableListOf(listOf(1)))

        kotMockImpl.handleFunctionCall<Unit>(function, 1)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should add to function call count with a multiple args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsNoReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))

        kotMockImpl.handleFunctionCall<Unit>(function, 1, "hello", true)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should return stored return value whenever`(){
        val kotMockImpl = KotMockImpl()

        kotMockImpl.apply { whenever(::functionWithoutArgsReturn) thenReturn 4 }

        assertEquals(4, kotMockImpl.functionWithoutArgsReturn())
    }

    @Test
    fun `should return throw provided value whenever is throwable`(){
        val kotMockImpl = KotMockImpl()

        kotMockImpl.apply {
            whenever(::functionWithoutArgsReturn) thenThrow NullPointerException()
        }

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
            KotMockImpl().apply { whenever(::functionWithoutArgsReturn) thenReturn null }
        }
    }
}