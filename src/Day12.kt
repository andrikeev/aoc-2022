fun main() {
    fun part1(input: List<String>): Int {
        return HeightsGrid.parse(input).countMinStepsFromS()
    }

    fun part2(input: List<String>): Int {
        return HeightsGrid.parse(input).countMinStepsFromA()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput).also { println("part1 test: $it") } == 31)
    check(part2(testInput).also { println("part2 test: $it") } == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private class HeightsGrid(
    val heights: Array<Array<Int>>,
    val startPoint: Pair<Int, Int>,
    val finishPoint: Pair<Int, Int>,
) {
    private val rows: Int by lazy { heights.size }
    private val columns: Int by lazy { heights[0].size }
    private operator fun Array<Array<Int>>.get(point: Pair<Int, Int>) = heights[point.first][point.second]

    fun countMinStepsFromS(): Int {
        val visited = countSteps()
        return visited.getValue(startPoint)
    }

    fun countMinStepsFromA(): Int {
        val visited = countSteps()
        return visited.filter { heights[it.key] == 0 }.minOf { it.value }
    }

    private fun countSteps(): Map<Pair<Int, Int>, Int> {
        val visited = mutableMapOf<Pair<Int, Int>, Int>()
        val toVisit = ArrayDeque<Pair<Int, Int>>()

        fun addToVisit(point: Pair<Int, Int>, steps: Int) {
            if (!visited.containsKey(point)) {
                visited[point] = steps
                toVisit.add(point)
            }
        }

        fun visit(i: Int, j: Int, height: Int, steps: Int) {
            val point = Pair(i, j)
            if (i < 0 || i == rows || j < 0 || j == columns || visited.contains(point)) return
            if (height <= heights[point] + 1) {
                addToVisit(point, steps)
            }
        }
        addToVisit(finishPoint, 0)
        while (toVisit.isNotEmpty()) {
            val point = toVisit.removeFirst()
            val steps = requireNotNull(visited[point]) + 1
            val height = heights[point]
            visit(point.first, point.second + 1, height, steps)
            visit(point.first, point.second - 1, height, steps)
            visit(point.first - 1, point.second, height, steps)
            visit(point.first + 1, point.second, height, steps)
        }
        return visited
    }

    companion object {
        fun parse(input: List<String>): HeightsGrid {
            lateinit var finishPoint: Pair<Int, Int>
            lateinit var startPoint: Pair<Int, Int>
            val points = Array(input.size) { i ->
                Array(input[i].length) { j ->
                    when (val c = input[i][j]) {
                        'S' -> {
                            startPoint = Pair(i, j)
                            'a' - 'a'
                        }

                        'E' -> {
                            finishPoint = Pair(i, j)
                            'z' - 'a'
                        }

                        else -> c - 'a'
                    }
                }
            }
            return HeightsGrid(points, startPoint, finishPoint)
        }
    }
}
