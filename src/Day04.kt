fun main() {

    fun contains(first: String, second: String): Boolean {
        val (aStart, aEnd) = first.split("-").map(String::toInt)
        val (bStart, bEnd) = second.split("-").map(String::toInt)

        return aStart <= bStart && aEnd >= bEnd || bStart <= aStart && bEnd >= aEnd
    }

    fun intersect(first: String, second: String): Boolean {
        val (aStart, aEnd) = first.split("-").map(String::toInt)
        val (bStart, bEnd) = second.split("-").map(String::toInt)

        return aStart in bStart..bEnd || bStart in aStart..aEnd
    }

    fun part1(input: List<String>): Int {
        return input.count { line ->
            val (first, second) = line.split(",")
            contains(first, second)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { line ->
            val (first, second) = line.split(",")
            intersect(first, second)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
