fun main() {

    fun part1(input: List<String>): Int {
        val grid = RocksGrid.parse(input)
        var counter = 0
        while (grid.addSandUnit()) {
            counter++
        }
        return counter
    }

    fun part2(input: List<String>): Int {
        val grid = RocksGrid.parse(input)
        var counter = 0
        while (grid.addSandUnit2()) {
            counter++
        }
        return counter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput).also { println("part1 test: $it") } == 24)
    check(part2(testInput).also { println("part2 test: $it") } == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private class RocksGrid(
    val blocked: MutableSet<Pair<Int, Int>>,
    private val floorLevel: Int,
) {
    fun addSandUnit(): Boolean {
        var pos = Pair(500, 0)
        fun hasBottom(): Boolean = blocked.any { it -> it.first == pos.first && it.second > pos.second }
        fun canFall(): Boolean {
            return !blocked.contains(Pair(pos.first, pos.second + 1)) ||
                    !blocked.contains(Pair(pos.first - 1, pos.second + 1)) ||
                    !blocked.contains(Pair(pos.first + 1, pos.second + 1))
        }

        while (canFall()) {
            if (!hasBottom()) return false
            val newPos = when {
                !blocked.contains(Pair(pos.first, pos.second + 1)) -> Pair(pos.first, pos.second + 1)
                !blocked.contains(Pair(pos.first - 1, pos.second + 1)) -> Pair(pos.first - 1, pos.second + 1)
                !blocked.contains(Pair(pos.first + 1, pos.second + 1)) -> Pair(pos.first + 1, pos.second + 1)
                else -> error("not possible")
            }
            pos = newPos
        }
        blocked.add(pos)
        return true
    }

    fun addSandUnit2(): Boolean {
        var pos = Pair(500, 0)
        fun canFall(): Boolean {
            return (pos.second < floorLevel - 1) &&
                    (!blocked.contains(Pair(pos.first, pos.second + 1)) ||
                            !blocked.contains(Pair(pos.first - 1, pos.second + 1)) ||
                            !blocked.contains(Pair(pos.first + 1, pos.second + 1)))
        }
        if (blocked.contains(pos)) {
            return false
        }
        while (canFall()) {
            val newPos = when {
                !blocked.contains(Pair(pos.first, pos.second + 1)) -> Pair(pos.first, pos.second + 1)
                !blocked.contains(Pair(pos.first - 1, pos.second + 1)) -> Pair(pos.first - 1, pos.second + 1)
                !blocked.contains(Pair(pos.first + 1, pos.second + 1)) -> Pair(pos.first + 1, pos.second + 1)
                else -> error("not possible")
            }
            pos = newPos
        }
        blocked.add(pos)
        return true
    }

    companion object {
        fun parse(input: List<String>): RocksGrid {
            val rocks = mutableSetOf<Pair<Int, Int>>()
            var floorLevel = 0
            input.forEach { line ->
                val points = line
                    .split(" -> ")
                    .map { it.split(",").map(String::toInt).let { (x, y) -> Pair(x, y) } }
                for (i in 1..points.lastIndex) {
                    val start = points[i - 1]
                    val end = points[i]
                    for (x in minOf(start.first, end.first)..maxOf(start.first, end.first)) {
                        for (y in minOf(start.second, end.second)..maxOf(start.second, end.second)) {
                            rocks.add(Pair(x, y))
                        }
                    }
                    val maxLevel = maxOf(start.second, end.second) + 2
                    if (maxLevel > floorLevel) {
                        floorLevel = maxLevel
                    }
                }
            }
            return RocksGrid(rocks, floorLevel)
        }
    }
}
