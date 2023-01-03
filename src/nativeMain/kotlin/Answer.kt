package native

import common.Answer as CommonAnswer


data class Answer(
    override var value : Any? = Unit,
    override var toBeThrown : Boolean = false
) : CommonAnswer
