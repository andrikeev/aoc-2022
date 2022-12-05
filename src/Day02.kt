fun main() {
    fun part1(input: List<String>): Int {
        val shapeScores = mapOf(
            'X' to 1,
            'Y' to 2,
            'Z' to 3,
        )
        val roundScores = mapOf(
            "A X" to 3,
            "A Y" to 6,
            "A Z" to 0,
            "B X" to 0,
            "B Y" to 3,
            "B Z" to 6,
            "C X" to 6,
            "C Y" to 0,
            "C Z" to 3,
        )
        return input.sumOf { line ->
            shapeScores.getValue(line[2]) + roundScores.getValue(line)
        }
    }

    fun part2(input: List<String>): Int {
        val roundScores = mapOf(
            'X' to 0,
            'Y' to 3,
            'Z' to 6,
        )
        val shapeScores = mapOf(
            "A X" to 3, // ls - A C
            "A Y" to 1, // dr - A A
            "A Z" to 2, // wn - A B
            "B X" to 1, // ls - B A
            "B Y" to 2, // dr - B B
            "B Z" to 3, // wn - B C
            "C X" to 2, // ls - C B
            "C Y" to 3, // dr - C C
            "C Z" to 1, // wn - C A
        )
        return input.sumOf { line ->
            shapeScores.getValue(line) + roundScores.getValue(line[2])
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
