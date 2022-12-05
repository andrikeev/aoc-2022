fun main() {
    fun part1(input: List<String>): Int {
        var maxCals = 0
        var curCal = 0
        input.forEachIndexed { index, line ->
            if (line.isNotBlank()) {
                curCal += line.toInt()
            }
            if (line.isBlank() || index == line.lastIndex) {
                if (curCal > maxCals) {
                    maxCals = curCal
                }
                curCal = 0
            }
        }
        return maxCals
    }

    fun part2(input: List<String>): Int {
        val topList = intArrayOf(0, 0, 0)
        var curCal = 0
        input.forEachIndexed { index, line ->
            if (line.isNotBlank()) {
                curCal += line.toInt()
            }
            if (line.isBlank() || index == input.lastIndex) {
                when {
                    curCal > topList[2] -> {
                        topList[0] = topList[1]
                        topList[1] = topList[2]
                        topList[2] = curCal
                    }
                    curCal > topList[1] -> {
                        topList[0] = topList[1]
                        topList[1] = curCal
                    }
                    curCal > topList[0] -> {
                        topList[0] = curCal
                    }
                }
                curCal = 0
            }
        }
        return topList.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
