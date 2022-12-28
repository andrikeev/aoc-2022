fun main() {

    fun part1(input: List<String>): Long {
        val monkeys = buildMap {
            input.forEach {
                val (name, job) = MonkeyJob.parse(it)
                put(name, job)
            }
        }

        fun MonkeyJob.Calculate.firstMonkey() = monkeys.getValue(monkey1)
        fun MonkeyJob.Root.firstMonkey() = monkeys.getValue(monkey1)
        fun MonkeyJob.Calculate.secondMonkey() = monkeys.getValue(monkey2)
        fun MonkeyJob.Root.secondMonkey() = monkeys.getValue(monkey2)

        fun MonkeyJob.getValue(): Long {
            return when (this) {
                is MonkeyJob.Calculate -> {
                    val monkey1Value = firstMonkey().getValue()
                    val monkey2Value = secondMonkey().getValue()
                    when (op) {
                        MonkeyJob.Op.Plus -> monkey1Value + monkey2Value
                        MonkeyJob.Op.Minus -> monkey1Value - monkey2Value
                        MonkeyJob.Op.Multiply -> monkey1Value * monkey2Value
                        MonkeyJob.Op.Divide -> monkey1Value / monkey2Value
                    }
                }

                is MonkeyJob.Yell -> this.value
                is MonkeyJob.Root -> firstMonkey().getValue() + secondMonkey().getValue()
            }
        }

        return monkeys.getValue("root").getValue()
    }

    fun part2(input: List<String>): Long {
        val me = "humn"
        val monkeys = buildMap {
            input.forEach {
                val (name, job) = MonkeyJob.parse(it)
                put(name, job)
            }
        }

        fun MonkeyJob.Calculate.firstMonkey() = monkeys.getValue(monkey1)
        fun MonkeyJob.Root.firstMonkey() = monkeys.getValue(monkey1)
        fun MonkeyJob.Calculate.secondMonkey() = monkeys.getValue(monkey2)
        fun MonkeyJob.Root.secondMonkey() = monkeys.getValue(monkey2)

        fun MonkeyJob.getValue(): Long {
            return when (this) {
                is MonkeyJob.Calculate -> {
                    val monkey1Value = firstMonkey().getValue()
                    val monkey2Value = secondMonkey().getValue()
                    when (op) {
                        MonkeyJob.Op.Plus -> monkey1Value + monkey2Value
                        MonkeyJob.Op.Minus -> monkey1Value - monkey2Value
                        MonkeyJob.Op.Multiply -> monkey1Value * monkey2Value
                        MonkeyJob.Op.Divide -> monkey1Value / monkey2Value
                    }
                }

                is MonkeyJob.Yell -> this.value
                is MonkeyJob.Root -> firstMonkey().getValue() + secondMonkey().getValue()
            }
        }

        fun MonkeyJob.findMe(): Boolean {
            return when (this) {
                is MonkeyJob.Calculate -> monkey1 == me || monkey2 == me ||
                        firstMonkey().findMe() || secondMonkey().findMe()

                is MonkeyJob.Yell -> false
                is MonkeyJob.Root -> false
            }
        }

        fun MonkeyJob.Calculate.getMyValue(correctValue: Long): Long {
            if (monkey1 == me) {
                val secondValue = secondMonkey().getValue()
                return when (op) {
                    MonkeyJob.Op.Plus -> correctValue - secondValue
                    MonkeyJob.Op.Minus -> correctValue + secondValue
                    MonkeyJob.Op.Multiply -> correctValue / secondValue
                    MonkeyJob.Op.Divide -> correctValue * secondValue
                }
            } else if (monkey2 == me) {
                val firstValue = firstMonkey().getValue()
                return when (op) {
                    MonkeyJob.Op.Plus -> correctValue - firstValue
                    MonkeyJob.Op.Minus -> firstValue - correctValue
                    MonkeyJob.Op.Multiply -> correctValue / firstValue
                    MonkeyJob.Op.Divide -> firstValue / correctValue
                }
            } else {
                val firstMonkey = firstMonkey()
                val secondMonkey = secondMonkey()
                return if (firstMonkey.findMe()) {
                    val secondValue = secondMonkey.getValue()
                    val newCorrectValue = when (op) {
                        MonkeyJob.Op.Plus -> correctValue - secondValue
                        MonkeyJob.Op.Minus -> correctValue + secondValue
                        MonkeyJob.Op.Multiply -> correctValue / secondValue
                        MonkeyJob.Op.Divide -> correctValue * secondValue
                    }
                    (firstMonkey as MonkeyJob.Calculate).getMyValue(newCorrectValue)
                } else {
                    val firstValue = firstMonkey.getValue()
                    val newCorrectValue = when (op) {
                        MonkeyJob.Op.Plus -> correctValue - firstValue
                        MonkeyJob.Op.Minus -> firstValue - correctValue
                        MonkeyJob.Op.Multiply -> correctValue / firstValue
                        MonkeyJob.Op.Divide -> firstValue / correctValue
                    }
                    (secondMonkey as MonkeyJob.Calculate).getMyValue(newCorrectValue)
                }
            }
        }

        return with(monkeys.getValue("root") as MonkeyJob.Root) {
            val firstMonkey = firstMonkey()
            val secondMonkey = secondMonkey()
            if (firstMonkey.findMe()) {
                val correctValue = secondMonkey.getValue()
                (firstMonkey as MonkeyJob.Calculate).getMyValue(correctValue)
            } else {
                val correctValue = firstMonkey.getValue()
                (secondMonkey as MonkeyJob.Calculate).getMyValue(correctValue)
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput).also { println("part1 test: $it") } == 152L)
    check(part2(testInput).also { println("part2 test: $it") } == 301L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}

private sealed interface MonkeyJob {
    enum class Op { Plus, Minus, Multiply, Divide }

    data class Yell(val value: Long) : MonkeyJob
    data class Calculate(
        val monkey1: String,
        val monkey2: String,
        val op: Op,
    ) : MonkeyJob

    data class Root(
        val monkey1: String,
        val monkey2: String,
    ) : MonkeyJob

    companion object {
        fun parse(input: String): Pair<String, MonkeyJob> {
            val items = input.split(" ")
            val name = items.first().dropLast(1)
            return name to when {
                name == "root" -> Root(
                    monkey1 = items[1],
                    monkey2 = items[3],
                )

                items.size == 4 -> Calculate(
                    monkey1 = items[1],
                    monkey2 = items[3],
                    op = parseOp(items[2]),
                )

                else -> Yell(items[1].toLong())
            }
        }

        private fun parseOp(input: String): Op = when (input) {
            "+" -> Op.Plus
            "-" -> Op.Minus
            "*" -> Op.Multiply
            "/" -> Op.Divide
            else -> error("wrong input")
        }
    }
}
