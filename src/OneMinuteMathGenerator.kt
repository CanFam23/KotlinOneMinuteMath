import kotlin.math.sqrt

/**
 * A single generated math problem.
 *
 * @property problem The formatted expression (e.g. "12/3" or "4+7").
 * @property answer The correct answer as a String.
 */
data class Problem(
    val problem: String,
    val answer: String
)

/**
 * Generates arithmetic problems intended to be solved quickly.
 *
 * Supported operation codes:
 *  - "a" = addition
 *  - "s" = subtraction
 *  - "m" = multiplication
 *  - "d" = division (always produces an integer answer)
 */
class OneMinuteMathGenerator {
    /**
     * Maps operation codes to their display symbols.
     */
    private val opSymbol = mapOf(
        "a" to "+",
        "s" to "-",
        "m" to "*",
        "d" to "/",
    )

    /**
     * Generates a list of random problems.
     *
     * @param minNum Minimum operand (inclusive).
     * @param maxNum Maximum operand (inclusive).
     * @param numQuestions Number of problems to generate.
     * @param operations Allowed operation codes ("a", "s", "m", "d").
     * @return A list of [Problem]. If validation fails, returns an empty list.
     */
    fun generateProblems(
        minNum: Int,
        maxNum: Int,
        numQuestions: Int,
        operations: List<String>
    ): List<Problem> {
        val errorMsg = validateParams(minNum, maxNum, numQuestions, operations)
        if (errorMsg.isNotEmpty()) {
            println(errorMsg)
            return emptyList()
        }

        // Build into a MutableList, then return as read-only List.
        val problems = mutableListOf<Problem>()

        repeat(numQuestions) {
            val opCode = operations.random().lowercase()

            if (opCode == "d") {
                // pick a numerator, then pick a divisor so the result is an integer.
                val numerator = (minNum..maxNum).random()

                val (cleanNumerator, denominator) = generateRandomDivisor(numerator)

                problems += Problem(
                    problem = "$cleanNumerator/${denominator}",
                    answer = (cleanNumerator / denominator).toString()
                )
            } else {
                val n1 = (minNum..maxNum).random()
                val n2 = (minNum..maxNum).random()

                // Compute using the operation code
                val answer = when (opCode) {
                    "a" -> n1 + n2
                    "s" -> n1 - n2
                    "m" -> n1 * n2
                    else -> 0
                }

                problems += Problem(
                    problem = "$n1${opSymbol.getValue(opCode)}$n2",
                    answer = answer.toString()
                )
            }
        }

        return problems
    }

    /**
     * Validates generator parameters.
     *
     * @return An error message if invalid; otherwise an empty string.
     */
    private fun validateParams(
        minNum: Int,
        maxNum: Int,
        numQuestions: Int,
        operations: List<String>
    ): String {
        if (minNum > maxNum) {
            return "Min number ($minNum) must be <= max number ($maxNum)"
        }

        if (numQuestions <= 0) {
            return "There should be at least one question"
        }

        if (operations.isEmpty()) {
            return "At least one operation must be provided"
        }

        val unknownOps = operations
            .map { it.lowercase() }
            .filter { it !in opSymbol.keys }

        if (unknownOps.isNotEmpty()) {
            return "${unknownOps.joinToString(", ")} ${
                if (unknownOps.size == 1) "is not a valid operation"
                else "are not valid operations"
            }"
        }

        return ""
    }

    /**
     * Returns (numerator, denominator) where denominator is a valid (non-zero) divisor of numerator.
     * This is used to ensure division problems always produce integer results.
     *
     * - Start at n; if needed, increment until we find a number with at least one valid divisor.
     * - Pick a random divisor from the divisor set.
     */
    private fun generateRandomDivisor(n: Int): Pair<Int, Int> {
        var num = n

        while (true) {
            val divisors = mutableListOf<Int>()

            // For divisor enumeration, use absolute for sqrt bound if num is negative.
            val absNum = kotlin.math.abs(num)

            // Find all divisors by scanning up to √|num|.
            val limit = sqrt(absNum.toDouble()).toInt()
            for (i in 1..limit) {
                if (absNum % i == 0) {
                    val other = absNum / i

                    // Add both factors (avoid duplicates when i == other).
                    divisors += i
                    if (other != i) divisors += other
                }
            }

            if (divisors.isNotEmpty()) {
                val denominator = divisors.random()
                return num to denominator
            }

            num += 1
        }
    }
}