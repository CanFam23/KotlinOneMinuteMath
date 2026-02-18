import kotlin.system.exitProcess

/**
 * Maximum number of questions the user may request in a single game.
 */
const val maxNumQuestions = 250

/**
 * Minimum allowed bound for user-selected numbers (exclusive in input validation).
 */
const val minNum = -100

/**
 * Maximum allowed bound for user-selected numbers (exclusive in input validation).
 */
const val maxNum = 100

/**
 * Total time limit for the game, in seconds.
 */
const val timeLimitSeconds = 60

/**
 * Allowed operation selection characters:
 * - 'a' addition
 * - 's' subtraction
 * - 'm' multiplication
 * - 'd' division
 */
val operations = setOf('a', 's', 'm', 'd')

/**
 * User-selected game configuration parameters.
 *
 * @property userMin Minimum value allowed for operands (does not apply to divisors).
 * @property userMax Maximum value allowed for operands (does not apply to divisors).
 * @property userNumQuestions Number of problems to generate for the game.
 * @property userOperations List of operation codes selected by the user.
 */
data class Result(
    val userMin: Int,
    val userMax: Int,
    val userNumQuestions: Int,
    val userOperations: List<Char>
)

/**
 * Pairing of a generated problem with the user's raw answer input.
 *
 * @property problem The problem presented to the user.
 * @property userAnswer The user's input string (may be empty or non-numeric).
 */
data class ProblemAnswers(
    val problem: Problem,
    val userAnswer: String
)

/**
 * Exits the program immediately if the user input indicates a quit request.
 *
 * The program uses 'q' as the quit command at any prompt.
 *
 * @param s User input string to check.
 */
fun checkQuit(s: String) {
    if (s == "q") {
        exitProcess(0)
    }
}

/**
 * Prompts the user for all configuration parameters needed to run a game:
 * minimum value, maximum value, number of questions, and included operations.
 *
 * Input is repeatedly requested until valid values are provided.
 *
 * Validation rules:
 * - Minimum must be in (minNum, maxNum)
 * - Maximum must be in (userMin, maxNum)
 * - Number of questions must be in [1, maxNumQuestions)
 * - Operations must be a non-empty combination of allowed operation codes
 *
 * @return Parsed and validated configuration values.
 */
fun getParams(): Result {
    var userMinNum: Int
    var userMaxNum: Int
    var userNumQuestions: Int
    var userOperations: List<Char>

    // Prompt for and validate the minimum operand bound.
    while (true) {
        println("Enter the smallest number that can be used in each problem (Doesn't apply to divisors): ")
        println("Given number must be between $minNum and $maxNum (Exclusive):")

        val userInput = readln().lowercase().trim()

        checkQuit(userInput)

        try {
            userMinNum = userInput.toInt()

            if ((userMinNum !in (minNum + 1)..<maxNum)) {
                System.err.println("$userMinNum is outside the bounds")
                System.err.println("Please enter a number between $minNum and $maxNum (Exclusive).")
                continue
            }
        } catch (_: NumberFormatException) {
            System.err.println("$userInput is not a number")
            System.err.println("Please enter a number between $minNum and $maxNum (Exclusive).")
            continue
        }
        break
    }

    // Prompt for and validate the maximum operand bound.
    while (true) {
        println("Enter the largest number that can be used in each problem (Doesn't apply to divisors): ")
        println("Given number must be between $userMinNum and $maxNum (Exclusive):")

        val userInput = readln().lowercase().trim()

        checkQuit(userInput)

        try {
            userMaxNum = userInput.toInt()

            if ((userMaxNum !in (userMinNum + 1)..<maxNum)) {
                System.err.println("$userMaxNum is outside the allowed bounds")
                System.err.println("Please enter a number between $userMinNum and $maxNum (Exclusive).")
                continue
            }
        } catch (_: NumberFormatException) {
            System.err.println("$userInput is not a number")
            System.err.println("Please enter a number between $userMinNum and $maxNum (Exclusive).")
            continue
        }
        break
    }

    // Prompt for and validate the number of questions.
    while (true) {
        println("Enter the number of problems you want to try and answer")
        println("Given number must be less than $maxNumQuestions:")

        val userInput = readln().lowercase().trim()

        checkQuit(userInput)

        try {
            userNumQuestions = userInput.toInt()

            if ((userNumQuestions !in (1)..<maxNumQuestions)) {
                System.err.println("$userNumQuestions is outside the allowed bounds")
                System.err.println("Please enter a number between 0 and $maxNumQuestions (Exclusive).")
                continue
            }
        } catch (_: NumberFormatException) {
            System.err.println("$userInput is not a number")
            System.err.println("Please enter a number between 0 and $maxNumQuestions (Exclusive).")
            continue
        }
        break
    }

    // Prompt for and validate the set of operations to include.
    while (true) {
        println(
            """
What operations do you want to be included?
    Enter any combination of
    - 'a' for addition
    - 's' for subtraction
    - 'm' for multiplication
    - 'd' for division
    (Ex. 'as' for addition and subtraction, or 'asmd' for all)
    Any duplicates will be ignored and input must be any combination of the above 4 letters.
Operation(s):
            """.trimIndent()
        )

        val userInput = readln().lowercase().trim()

        checkQuit(userInput)

        // Convert to a set to remove duplicates and simplify validation.
        val userOpSet: Set<Char> = userInput.toSet()

        // Reject any characters not in the allowed operation set.
        if (!operations.containsAll(userOpSet)) {
            val invalidOps = userOpSet.filter { it !in operations }
            System.err.println(
                "${invalidOps.joinToString(",")} ${
                    if (invalidOps.size == 1) "is not a valid operation"
                    else "are not valid operations"
                }"
            )
            continue
        }

        userOperations = userOpSet.toList()
        break
    }

    return Result(userMinNum, userMaxNum, userNumQuestions, userOperations)
}

/**
 * Runs the timed question-and-answer loop for the given list of problems.
 *
 * Behavior:
 * - Waits for the user to type "start"
 * - Starts a single global timer for the entire game
 * - Stops early if the time limit is reached
 * - Stores each raw user input string as the answer for the corresponding problem index
 *
 * @param problems Problems to present to the user, in order.
 * @return A mutable list of user answer strings aligned by index with [problems].
 */
fun runGame(problems: List<Problem>): MutableList<String> {
    // Pre-allocate answers as empty strings to preserve positional alignment.
    val userAnswers = MutableList(problems.size) { "" }

    // Gate the start of the timer on an explicit "start" command.
    while (true) {
        println("Type 'start' when you're ready to start")

        val userInput = readln().lowercase().trim()

        checkQuit(userInput)

        if (userInput != "start") {
            System.err.println("$userInput is not a valid input")
            continue
        }

        break
    }

    // Record the start time once; the timer applies to the full sequence of problems.
    val startTime = System.currentTimeMillis() / 1000

    // Iterate through problems until time expires or the list is exhausted.
    for (i in problems.indices) {
        var timeLeft = timeLimitSeconds - ((System.currentTimeMillis() / 1000) - startTime)
        if (timeLeft <= 0) {
            println("Times up!")
            break
        }

        println("$timeLeft seconds remaining")
        println("${problems[i].problem}=")

        val userInput = readln().lowercase().trim()

        // Re-check time immediately after input to avoid counting answers entered after the limit.
        timeLeft = timeLimitSeconds - ((System.currentTimeMillis() / 1000) - startTime)
        if (timeLeft <= 0) {
            println("Times up!")
            break
        }

        userAnswers[i] = userInput
    }

    // If the user answered the last slot and time remains, report leftover time.
    val timeLeft = timeLimitSeconds - ((System.currentTimeMillis() / 1000) - startTime)
    if (userAnswers[userAnswers.size - 1] != "" && timeLeft > 0) {
        println("You finished with $timeLeft seconds to spare!")
    }

    return userAnswers
}

/**
 * Grades user answers against the provided problems and prints a summary report.
 *
 * Rules:
 * - Empty answer counts as unanswered
 * - Non-numeric answers count as incorrect
 * - Numeric answers are compared to the problem's integer answer
 *
 * The report includes:
 * - Total correct out of total
 * - Unanswered count (if any)
 * - List of incorrect problems with correct answer and user input
 *
 * @param problems The problems that were asked.
 * @param userAnswers The user's raw answers aligned by index with [problems].
 */
fun calcShowResults(problems: List<Problem>, userAnswers: List<String>) {
    var correct = 0
    var unanswered = 0
    val incorrect = mutableListOf<ProblemAnswers>()

    // Evaluate each response against the corresponding problem.
    for (i in problems.indices) {
        try {
            if (userAnswers[i].isEmpty()) {
                unanswered++
            } else if (problems[i].answer.toInt() == userAnswers[i].toInt()) {
                correct++
            } else {
                incorrect += ProblemAnswers(problems[i], userAnswers[i])
            }
        } catch (_: NumberFormatException) {
            incorrect += ProblemAnswers(problems[i], userAnswers[i])
        }
    }

    // Print summary block.
    println()
    println("=".repeat(20))
    println()

    when (correct) {
        problems.size -> {
            println("You got every problem right!")
        }

        0 if unanswered == 0 -> {
            println("You didn't get any problems right... awkward")
        }

        else -> {
            println("You got $correct/${problems.size} problems correct.")
        }
    }

    if (unanswered > 0) println("You failed to answer $unanswered problem(s)")

    // Print details for incorrect answers.
    if (incorrect.isNotEmpty()) {
        println()
        println("You got ${incorrect.size} problems wrong")

        for (problem in incorrect) {
            println("${problem.problem.problem} = ${problem.problem.answer}, you answered ${problem.userAnswer}")
        }
    }
}

/**
 * Program entry point.
 *
 * Flow:
 * - Print instructions
 * - Read user configuration parameters
 * - Generate problems using [OneMinuteMathGenerator]
 * - Run the timed game session
 * - Grade and display results
 */
fun main() {
    // Problem generator instance used to create the question set.
    val ommg = OneMinuteMathGenerator()

    println()
    println("Welcome to the One Minute Math Challenge!")
    println()
    println("You will have 60 seconds to solve as many problems as possible.")
    println("To quit at any time, press 'q'.")
    println("To answer a question, enter your answer and then press 'Enter' or 'Return'.")
    println("NOTE: A blank or empty answer will count as a unanswered question.")
    println()

    val result = getParams()

    val problems = ommg.generateProblems(
        result.userMin,
        result.userMax,
        result.userNumQuestions,
        result.userOperations
    )

    val userAnswers = runGame(problems)

    calcShowResults(problems, userAnswers)
}