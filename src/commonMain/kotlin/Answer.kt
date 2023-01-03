package common

interface Answer {
    operator fun component1(): Any?
    operator fun component2(): Boolean
    val value: Any?
    val toBeThrown : Boolean
}