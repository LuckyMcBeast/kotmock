import common.ReturnTypeException
import native.FunctionCall
import kotlin.test.*

class FunctionCallTest {

    @Test
    fun `should return true if called with same args`() {
        val functionCall = FunctionCall(function = DummyClass::noReturn)
        functionCall.args.add(listOf(1, true, "hello"))

        assertTrue { functionCall.wasCalledWith(listOf(1, true, "hello")).first }
    }

    @Test
    fun `should return false if was not called with same args`() {
        val functionCall = FunctionCall(function = DummyClass::noReturn)
        functionCall.args.add(listOf(2, false))

        assertFalse { functionCall.wasCalledWith(listOf(1, true, "hello")).first }
    }

    @Test
    fun `should return false if args are not in the same order`() {
        val functionCall = FunctionCall(function = DummyClass::noReturn)
        functionCall.args.add(listOf(true, 1, "hello"))

        assertFalse { functionCall.wasCalledWith(listOf(1, true, "hello")).first }
    }

    @Test
    fun `should return true if called with same args for the amount of times specified`() {
        val functionCall = FunctionCall(function = DummyClass::noReturn)
        functionCall.args.add(listOf(1, true, "hello"))
        functionCall.args.add(listOf(1, true, "hello"))

        assertTrue { functionCall.wasCalledWith(listOf(1, true, "hello"), 2).first }
    }

    @Test
    fun `should return false if called with same args but not for the amount of times specified`() {
        val functionCall = FunctionCall(function = DummyClass::noReturn)
        functionCall.args.add(listOf(1, true, "hello"))
        functionCall.args.add(listOf(1, true, "hello"))

        assertFalse { functionCall.wasCalledWith(listOf(1, true, "hello"), 1).first }
    }

    @Test
    fun `should set returns if value matches  function = `() {
        val functionCall = FunctionCall(function = DummyClass::functionThatReturnsInt)

        functionCall.thenReturn(4)

        assertEquals(4, functionCall.returnOrThrow())
    }

    @Test
    fun `should set throw exception if value does not match  function = `() {
        val functionCall = FunctionCall(function = DummyClass::functionThatReturnsInt)

        assertFailsWith<ReturnTypeException> { functionCall.thenReturn("not int") }
    }

    @Test
    fun `should set throwable`() {
        val functionCall = FunctionCall(function = DummyClass::noReturn)
        val message = "This is an exception"

        functionCall.thenThrow(Exception(message))

        assertFailsWith<Exception> { functionCall.returnOrThrow() }
    }

    @Test
    fun `should answer in order when multiples returns or throwables are provided`(){
        val functionCall = FunctionCall(function = DummyClass::functionThatReturnsInt)

        functionCall.thenReturn(1)
        functionCall.thenReturn(2)
        functionCall.thenReturn(3)
        functionCall.thenThrow(Exception())
        functionCall.thenThrow(RuntimeException())

        assertEquals(1, functionCall.returnOrThrow())
        assertEquals(2, functionCall.returnOrThrow())
        assertEquals(3, functionCall.returnOrThrow())
        assertFailsWith<Exception> { functionCall.returnOrThrow() }
        assertFailsWith<RuntimeException> { functionCall.returnOrThrow() }

    }
}


class DummyClass {
    fun functionThatReturnsInt(): Int {
        return 1
    }
    fun noReturn() {}
}
