fun main() {

    fun List<String>.parseMonkeys(): List<Monkey> {
        return plus("").chunked(7).map { strings ->
            Monkey(
                items = strings[1]
                    .substringAfter("Starting items: ")
                    .split(", ")
                    .map(String::toLong)
                    .toMutableList(),
                operation = MonkeyOp.parse(strings[2]),
                test = MonkeyTest.parse(strings.subList(3, 6)),
            )
        }
    }

    fun part1(input: List<String>): Int {
        val monkeys = input.parseMonkeys()
        repeat(20) {
            monkeys.forEach { monkey ->
                monkey.items.apply {
                    forEach { item ->
                        val newWorryLevel = monkey.operation(item) / 3
                        val direction = monkey.test(newWorryLevel)
                        monkey.counter++
                        monkeys[direction].items.add(newWorryLevel)
                    }
                    clear()
                }
            }
        }
        return monkeys
            .sortedByDescending(Monkey::counter)
            .take(2)
            .map(Monkey::counter)
            .let { (first, second) -> first * second }
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.parseMonkeys()
        val modulo = monkeys.fold(1) { acc, monkey -> acc * monkey.test.modulo }
        repeat(10000) {
            monkeys.forEach { monkey ->
                monkey.items.apply {
                    forEach { item ->
                        val newWorryLevel = monkey.operation(item) % modulo
                        val direction = monkey.test(newWorryLevel)
                        monkey.counter++
                        monkeys[direction].items.add(newWorryLevel)
                    }
                    clear()
                }
            }
        }
        return monkeys
            .sortedByDescending(Monkey::counter)
            .take(2)
            .map(Monkey::counter)
            .let { (first, second) -> first.toLong() * second }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput).also { println("part1 test: $it") } == 10605)
    check(part2(testInput).also { println("part2 test: $it") } == 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private class Monkey(
    val items: MutableList<Long>,
    val operation: MonkeyOp,
    val test: MonkeyTest,
    var counter: Int = 0,
)

private sealed interface MonkeyOp {
    operator fun invoke(worryLevel: Long): Long
    class Add(private val addition: Int) : MonkeyOp {
        override fun invoke(worryLevel: Long) = worryLevel + addition

        companion object {
            val regex = Regex("new = old [+] (\\d+)")
        }
    }

    class Multiply(private val multiplyer: Int) : MonkeyOp {
        override fun invoke(worryLevel: Long) = worryLevel * multiplyer

        companion object {
            val regex = Regex("new = old [*] (\\d+)")
        }
    }

    object Square : MonkeyOp {
        override fun invoke(worryLevel: Long) = worryLevel * worryLevel
        val regex = Regex("new = old [*] old")
    }

    companion object {
        fun parse(input: String): MonkeyOp {
            val findAdd = Add.regex.find(input)
            val findMultiply = Multiply.regex.find(input)
            val findSquare = Square.regex.find(input)
            return when {
                findAdd != null -> Add(findAdd.groups[1]!!.value.toInt())
                findMultiply != null -> Multiply(findMultiply.groups[1]!!.value.toInt())
                findSquare != null -> Square
                else -> error("not known operation: $input")
            }
        }
    }
}

private class MonkeyTest(
    val modulo: Int,
    private val positiveResult: Int,
    private val negativeResult: Int,
) {
    operator fun invoke(worryLevel: Long): Int = if (worryLevel % modulo == 0L) {
        positiveResult
    } else {
        negativeResult
    }

    companion object {
        fun parse(input: List<String>): MonkeyTest {
            return MonkeyTest(
                modulo = input[0].split(" ").last().toInt(),
                positiveResult = input[1].split(" ").last().toInt(),
                negativeResult = input[2].split(" ").last().toInt(),
            )
        }
    }
}