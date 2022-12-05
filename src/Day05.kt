fun main() {

    fun readScheme(input: List<String>): Array<MutableList<Char>> {
        val numberOfCrates = (input.first().length + 1) / 4
        val maxHeight = input.indexOfFirst(String::isEmpty) - 1
        val crates = Array(numberOfCrates) { mutableListOf<Char>() }
        input.take(maxHeight).forEach { line ->
            ("$line ").chunked(4)
                .map { it[1] }
                .forEachIndexed { index, c ->
                    if (c != ' ') {
                        crates[index].add(0, c)
                    }
                }
        }
        return crates
    }

    fun readCommands(input: List<String>): List<Triple<Int, Int, Int>> {
        val commandsStartLine = input.indexOfFirst(String::isEmpty) + 1
        return input.drop(commandsStartLine)
            .map { line ->
                val words = line.split(" ")
                val count = words[1].toInt()
                val from = words[3].toInt() - 1
                val to = words[5].toInt() - 1
                Triple(count, from, to)
            }
    }

    fun part1(input: List<String>): String {
        val crates = readScheme(input)
        readCommands(input).forEach { (count, from, to) ->
            repeat(count) {
                crates[to].add(crates[from].removeLast())
            }
        }
        return crates.joinToString("") { it.last().toString() }
    }

    fun part2(input: List<String>): String {
        val crates = readScheme(input)
        readCommands(input).forEach { (count, from, to) ->
            val fromCrate = crates[from]
            val stack = fromCrate.subList(fromCrate.lastIndex - count + 1, fromCrate.lastIndex + 1)
            crates[to].addAll(stack)
            stack.clear()
        }
        return crates.joinToString("") { it.last().toString() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
