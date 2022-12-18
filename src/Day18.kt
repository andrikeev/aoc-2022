import kotlin.math.abs

fun main() {

    data class Cube(val x: Int, val y: Int, val z: Int)

    fun List<String>.parse(): List<Cube> {
        return map {
            val (x, y, z) = it.split(",").map(String::toInt)
            Cube(x, y, z)
        }
    }

    fun part1(input: List<String>): Int {
        fun Cube.isConnectedTo(cube: Cube): Boolean {
            return abs(cube.x - x) == 1 && cube.y == y && cube.z == z ||
                    abs(cube.y - y) == 1 && cube.x == x && cube.z == z ||
                    abs(cube.z - z) == 1 && cube.y == y && cube.x == x
        }
        return with(input.parse().toSet()) {
            sumOf { cube -> 6 - count(cube::isConnectedTo) }
        }
    }

    fun part2(input: List<String>): Int {
        val rock = input.parse().toSet()
        var surfaces = 0
        val xx = rock.minOf(Cube::x) - 1..rock.maxOf(Cube::x) + 1
        val yy = rock.minOf(Cube::y) - 1..rock.maxOf(Cube::y) + 1
        val zz = rock.minOf(Cube::z) - 1..rock.maxOf(Cube::z) + 1
        val visited = mutableSetOf<Cube>()
        val queue = ArrayDeque<Cube>(1).apply {
            add(Cube(xx.first, yy.first, zz.first))
        }

        fun enqueue(cube: Cube) {
            if (cube.x in xx && cube.y in yy && cube.z in zz) {
                queue.addLast(cube)
                visited.add(cube)
            }
        }

        fun Cube.neighbours(visited: Set<Cube>): Set<Cube> = buildSet {
            fun tryAdd(cube: Cube) {
                if (cube !in visited) add(cube)
            }
            tryAdd(copy(x = x - 1))
            tryAdd(copy(x = x + 1))
            tryAdd(copy(y = y - 1))
            tryAdd(copy(y = y + 1))
            tryAdd(copy(z = z - 1))
            tryAdd(copy(z = z + 1))
        }

        while (queue.isNotEmpty()) {
            queue.removeFirst()
                .neighbours(visited)
                .forEach { cube ->
                    if (cube in rock) {
                        surfaces++
                    } else {
                        enqueue(cube)
                    }
                }
        }
        return surfaces
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput).also { println("part1 test: $it") } == 64)
    check(part2(testInput).also { println("part2 test: $it") } == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
