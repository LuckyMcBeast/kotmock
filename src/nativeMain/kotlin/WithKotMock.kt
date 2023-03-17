package native

import common.VerificationType

open class WithKotMock {
    fun <T : KotMock> verify(
        kotMock: T,
        times: Int? = null,
        type: VerificationType = VerificationType.EXACTLY
    ): T {
        return times?.let { kotMock.enableVerificationMode(times = it, type = type) as T }
            ?: kotMock.enableVerificationMode(times = 1, type = VerificationType.AT_LEAST) as T
    }
}


//verify(kotMockImpl, times = 2).functionThatHasInt(1) <- Good to Go

//Desired: whenever(kotMock).function(arg1,arg2) thenReturn 5
//Interim: kotMockImpl.apply { whenever(::functionWithArgsAndReturn).withArgs() thenReturn 1 }
//Current: kotMockImpl.apply { whenever(::functionWithoutArgsAndReturn) thenReturn 1 }