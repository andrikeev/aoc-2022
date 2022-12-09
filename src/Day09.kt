fun main() {

    fun String.toCommand(): Command = split(" ")
        .let { (dir, steps) -> Command(Direction.valueOf(dir), steps.toInt()) }
    fun Position.move(direction: Direction) {
        when (direction) {
            Direction.L -> x--
            Direction.U -> y++
            Direction.R -> x++
            Direction.D -> y--
        }
    }
    fun adjustTail(head: Position, tail: Position) {
        when {
            head.x - tail.x == 2 -> {
                tail.x++
                when {
                    tail.y > head.y -> tail.y--
                    tail.y < head.y -> tail.y++
                }
            }

            head.x - tail.x == -2 -> {
                tail.x--
                when {
                    tail.y > head.y -> tail.y--
                    tail.y < head.y -> tail.y++
                }
            }

            head.y - tail.y == 2 -> {
                when {
                    tail.x > head.x -> tail.x--
                    tail.x < head.x -> tail.x++
                }
                tail.y++
            }

            head.y - tail.y == -2 -> {
                when {
                    tail.x > head.x -> tail.x--
                    tail.x < head.x -> tail.x++
                }
                tail.y--
            }
        }
    }

    fun part1(input: List<String>): Int {
        val head = Position(0, 0)
        val tail = Position(0, 0)
        return input.asSequence()
            .map(String::toCommand)
            .map { command ->
                sequence {
                    repeat(command.steps) {
                        head.move(command.direction)
                        adjustTail(head, tail)
                        yield(tail.copy())
                    }
                }
            }
            .flatten()
            .toSet()
            .count()
    }

    fun part2(input: List<String>): Int {
        val head = Position(0, 0)
        val knots = Array(9) { Position(0, 0) }
        return input.asSequence()
            .map(String::toCommand)
            .map { command ->
                sequence {
                    repeat(command.steps) {
                        head.move(command.direction)
                        knots.forEachIndexed { index, knot ->
                            val prevKnot = if (index == 0) head else knots[index - 1]
                            adjustTail(prevKnot, knot)
                            if (index == knots.lastIndex) {
                                yield(knot.copy())
                            }
                        }
                    }
                }
            }
            .flatten()
            .toSet()
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput).also { println(it) } == 13)
    val testInput2 = readInput("Day09_test2")
    check(part2(testInput2).also { println(it) } == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private data class Command(val direction: Direction, val steps: Int)
private enum class Direction { R, U, L, D, }
private data class Position(var x: Int, var y: Int)
