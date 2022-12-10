fun main() {

    fun List<String>.mapToOps(): List<Op> =
        this.map { it.split(" ") }
            .map { if (it[0] == "noop") Op.Nop else Op.AddX(it[1].toInt()) }

    fun part1(input: List<String>): Int {
        val timestamps = setOf(20, 60, 100, 140, 180, 220)
        return sequence {
            var cyclesCounter = 1
            var x = 1
            input
                .mapToOps()
                .forEach { op ->
                    cyclesCounter++
                    if (cyclesCounter in timestamps) {
                        yield(cyclesCounter * x)
                    }
                    if (op is Op.AddX) {
                        cyclesCounter++
                        x += op.value
                        if (cyclesCounter in timestamps) {
                            yield(cyclesCounter * x)
                        }
                    }
                }
        }.sum()
    }

    fun part2(input: List<String>) {
        val sprite = Sprite()
        var cyclesCounter = 0
        var x = 1
        fun tick() {
            val drawPixelIndex = cyclesCounter % 40
            if (drawPixelIndex in sprite.pixels) {
                print("#")
            } else {
                print(".")
            }
            cyclesCounter++
            if (cyclesCounter % 40 == 0) {
                println()
            }
        }
        input
            .mapToOps()
            .forEach { op ->
                when (op) {
                    is Op.Nop -> tick()
                    is Op.AddX -> {
                        tick()
                        tick()
                        x += op.value
                        sprite.position = x
                    }
                }
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput).also { println(it) } == 13140)
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}

private sealed interface Op {
    val length: Int
    val value: Int

    data class AddX(override val value: Int) : Op {
        override val length = 2
    }

    object Nop : Op {
        override val length = 1
        override val value = 0
    }
}

private class Sprite {
    var position: Int = 1
    val pixels: IntRange
        get() = (position - 1..position + 1)
}
