package common

enum class VerificationType {
    EXACTLY, AT_LEAST, AT_MOST;

    fun evaluate(count: Int, times: Int): Boolean {
        return when (this) {
            EXACTLY -> count == times
            AT_LEAST -> count >= times
            AT_MOST -> count <= times
        }
    }
}