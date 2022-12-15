import kotlin.math.abs

fun main() {
    val regex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    fun Pair<Int, Int>.distanceTo(that: Pair<Int, Int>): Int {
        return abs(this.first - that.first) + abs(this.second - that.second)
    }

    fun String.parse(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val (sX, sY, bX, bY) = regex.find(this)!!.groupValues.drop(1).map(String::toInt)
        return Pair(sX, sY) to Pair(bX, bY)
    }

    fun part1(input: List<String>, level: Int): Int {
        val covered = mutableSetOf<Pair<Int, Int>>()
        input.forEach { line ->
            val (sensor, beacon) = line.parse()
            val distance = sensor.distanceTo(beacon)
            for (x in (sensor.first - distance)..(sensor.first + distance)) {
                val point = Pair(x, level)
                if (point != beacon && sensor.distanceTo(point) <= distance) {
                    covered.add(point)
                }
            }
        }
        return covered.size
    }

    fun part2(input: List<String>, maxLevel: Int): Long {
        val sensors = input.map { line ->
            val (sensor, beacon) = line.parse()
            sensor to sensor.distanceTo(beacon)
        }.sortedBy { (sensor, beacon) -> sensor.first }
        for (y in 0..maxLevel) {
            var x = 0
            sensors.forEach { (sensor, distance) ->
                if (sensor.distanceTo(Pair(x, y)) <= distance) {
                    x = sensor.first + distance - abs(sensor.second - y) + 1
                }
            }
            if (x <= maxLevel) {
                return x * 4_000_000L + y
            }
        }
        error("Not found")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10).also { println("part1 test: $it") } == 26)
    check(part2(testInput, 20).also { println("part2 test: $it") } == 56_000_011L)

    val input = readInput("Day15")
    println(part1(input, 2000000))
    println(part2(input, 4000000))
}
