fun main() {

    fun part1(input: List<String>): Int {
        val grid = Grid.from(input)
        var visibleCount = 0
        repeat(grid.height) { i ->
            repeat(grid.width) { j ->
                if (grid.isVisible(i, j)) {
                    visibleCount++
                }
            }
        }
        return visibleCount
    }

    fun part2(input: List<String>): Int {
        val grid = Grid.from(input)
        var maxScenicScore = 0
        repeat(grid.height) { i ->
            repeat(grid.width) { j ->
                val scenicScore = grid.getScenicScore(i, j)
                if (scenicScore > maxScenicScore) {
                    maxScenicScore = scenicScore
                }
            }
        }
        return maxScenicScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private class Grid(
    val height: Int,
    val width: Int,
    private val grid: Array<Array<Int>>,
) {
    fun isVisible(i: Int, j: Int): Boolean {
        if (i == 0 || i == (height - 1) || j == 0 || j == (width - 1)) {
            return true
        }
        val currentHeight = grid[i][j]
        var visibleFromRight = true
        var visibleFromLeft = true
        var visibleFromTop = true
        var visibleFromBottom = true

        for (k in 0 until j) {
            if (grid[i][k] >= currentHeight) {
                visibleFromRight = false
                break
            }
        }
        for (k in j + 1 until width) {
            if (grid[i][k] >= currentHeight) {
                visibleFromLeft = false
                break
            }
        }
        for (k in 0 until i) {
            if (grid[k][j] >= currentHeight) {
                visibleFromTop = false
                break
            }
        }
        for (k in i + 1 until height) {
            if (grid[k][j] >= currentHeight) {
                visibleFromBottom = false
                break
            }
        }

        return visibleFromRight || visibleFromLeft || visibleFromTop || visibleFromBottom
    }

    fun getScenicScore(i: Int, j: Int): Int {
        if (i == 0 || i == (height - 1) || j == 0 || j == (width - 1)) {
            return 0
        }
        val currentHeight = grid[i][j]
        var topScore = 0
        var rightScore = 0
        var leftScore = 0
        var bottomScore = 0

        for (k in i - 1 downTo 0) {
            topScore++
            if (grid[k][j] >= currentHeight) {
                break
            }
        }
        for (k in j - 1 downTo 0) {
            rightScore++
            if (grid[i][k] >= currentHeight) {
                break
            }
        }
        for (k in j + 1 until width) {
            leftScore++
            if (grid[i][k] >= currentHeight) {
                break
            }
        }
        for (k in i + 1 until height) {
            bottomScore++
            if (grid[k][j] >= currentHeight) {
                break
            }
        }

        return topScore * rightScore * leftScore * bottomScore
    }

    companion object {
        fun from(input: List<String>): Grid {
            val height = input.size
            val width = input[0].length
            val grid = Array(height) { i ->
                Array(width) { j ->
                    input[i][j].digitToInt()
                }
            }
            return Grid(height, width, grid)
        }
    }
}