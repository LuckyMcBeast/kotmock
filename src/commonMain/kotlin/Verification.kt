package common

interface Verification {
    val kotMock: KotMock
    var functionCall: FunctionCall?
    var args: List<Any?>
    val type: VerificationType
    val times: Int
    fun wasCalled()
}