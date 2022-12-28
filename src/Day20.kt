import java.util.LinkedList

fun main() {

    fun part1(input: List<String>): Long {
        val nodes = input.map(String::toLong).mapIndexed(::Node)
        return InfiniteList(nodes).apply { mix() }.result()
    }

    fun part2(input: List<String>): Long {
        val nodes = input.map(String::toLong).map { it * 811589153 }.mapIndexed(::Node)
        return InfiniteList(nodes).apply { repeat(10) { mix() } }.result()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput).also { println("part1 test: $it") } == 3L)
    check(part2(testInput).also { println("part2 test: $it") } == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

private data class Node(val id: Int, val value: Long)

private class InfiniteList(private val nodes: List<Node>) {
    private val list = LinkedList<Node>().apply { addAll(nodes) }

    private fun realIndex(index: Long): Int {
        return ((list.lastIndex + (index % list.lastIndex)) % list.lastIndex).toInt()
    }

    private fun <T> LinkedList<T>.moveItem(from: Int, to: Int) {
        if (to != from) {
            add(to, removeAt(from))
        }
    }

    fun mix() {
        nodes.forEach { node ->
            with(list) {
                val from = indexOf(node)
                val value = node.value
                val to = realIndex(from + value)
                moveItem(from, to)
            }
        }
    }

    fun result(): Long {
        val indexOfZero = list.indexOfFirst { it.value == 0L }
        return listOf(1000, 2000, 3000).sumOf { list[(indexOfZero + it) % list.size].value }
    }
}