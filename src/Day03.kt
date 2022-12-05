fun main() {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    fun priority(char: Char) = chars.indexOf(char) + 1

    fun part1(input: List<String>): Int {
        return input
            .map { line ->
                val first = line.take(line.length / 2).toSet()
                val second = line.drop(line.length / 2).toSet()
                first.intersect(second)
            }
            .flatten()
            .sumOf(::priority)
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .map { group ->
                group
                    .map(String::toSet)
                    .reduceRight { s, acc -> acc.intersect(s) }
                    .single()
            }
            .sumOf(::priority)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
