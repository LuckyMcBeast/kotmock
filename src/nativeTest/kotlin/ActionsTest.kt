import helper.KotMockImpl
import native.FunctionCall
import native.WithKotMock
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFails
import kotlin.test.assertTrue


class ActionsTest : WithKotMock() {

    @Test
    fun `should produce verification within KotMock`() {
        val kotMockImpl = KotMockImpl()

        assertTrue { kotMockImpl.verification == null }
        assertTrue { verify(kotMockImpl).verification != null }
    }

    @Test
    fun `should verify that function was called`() {
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(6)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        verify(kotMockImpl).functionWithArgsReturn(1,"hello", true)

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

        verify(kotMockImpl, times = 3).functionWithoutArgsOrReturn()

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should throw exception when can't verify that function was called`() {
        val kotMockImpl = KotMockImpl()

        assertFails { verify(kotMockImpl).functionWithoutArgsOrReturn() }
    }

    @Test
    fun `should throw exception when provided times does not match`() {
        val kotMockImpl = KotMockImpl()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()

        assertFails { verify(kotMockImpl, times = 2).functionWithoutArgsOrReturn() }
        assertFails { verify(kotMockImpl, times = 10).functionWithoutArgsOrReturn() }
        verify(kotMockImpl, times = 3).functionWithoutArgsOrReturn()
    }

    @Test
    fun `should verify that function is called at all without times provided`(){
        val kotMockImpl = KotMockImpl()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithoutArgsOrReturn()

        verify(kotMockImpl, times = 2).functionWithoutArgsOrReturn()
        verify(kotMockImpl).functionWithoutArgsOrReturn()
    }

    @Test
    fun `should verify that function was called with provided args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(3)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        verify(kotMockImpl).functionWithArgsReturn(1, "hello", true)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should should throw exception when can not verify function was called with provided args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(1)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        assertFails { verify(kotMockImpl).functionWithArgsReturn(2, "goodbye", false) }

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should should throw exception when has correct times but wrong args`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsNoReturn
        val expected = FunctionCall(function, 2, mutableListOf(listOf(1, "hello", true), listOf(1, "hello", true)))
        kotMockImpl.functionWithArgsNoReturn(1, "hello", true)
        kotMockImpl.functionWithArgsNoReturn(1, "hello", true)

        assertFails { verify(kotMockImpl, times = 2).functionWithArgsNoReturn(2, "goodbye", false) }

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should should throw exception when has correct args but wrong times`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithArgsReturn
        val expected = FunctionCall(function,1, mutableListOf(listOf(1, "hello", true)))
        kotMockImpl.whenever(function).thenReturn(4)
        kotMockImpl.functionWithArgsReturn(1, "hello", true)

        assertFails { verify(kotMockImpl, times = 2).functionWithArgsReturn( 1, "hello", true) }

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should allow null arguments and returns when nullable`(){
        val kotMockImpl = KotMockImpl()
        val function = kotMockImpl::functionWithNullableArgsAndReturn
        val expected = FunctionCall(function, 1, mutableListOf(listOf(null, null, null)))
        kotMockImpl.whenever(function).thenReturn(null)
        kotMockImpl.functionWithNullableArgsAndReturn(null, null, null)

        verify(kotMockImpl).functionWithNullableArgsAndReturn(null, null, null)

        assertContains(kotMockImpl.memberFunctionList, expected)
    }

    @Test
    fun `should verify total interactions`(){
        val kotMockImpl = KotMockImpl()
        kotMockImpl.functionWithoutArgsOrReturn()
        kotMockImpl.functionWithArgNoReturn(4)

        verify(kotMockImpl, times = 2).wasCalled()
    }

    @Test
    fun `should verify zero interactions`(){
        val kotMockImpl = KotMockImpl()

        verify(kotMockImpl, times = 0).wasCalled()
    }
}