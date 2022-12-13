import java.util.Stack

fun main() {

    fun List<String>.readPacketPairs(): List<Pair<DataValue, DataValue>> = plus("")
        .chunked(3)
        .map { Pair(DataValue.parse(it[0]), DataValue.parse(it[1])) }

    fun List<String>.readPackets(): List<DataValue> = filter(String::isNotEmpty)
        .map(DataValue.Companion::parse)

    fun part1(input: List<String>): Int {
        return input.readPacketPairs()
            .mapIndexed { index, pair ->
                if (pair.first < pair.second) {
                    index + 1
                } else {
                    0
                }
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val divider1 = DataValue.parse("[[2]]")
        val divider2 = DataValue.parse("[[6]]")
        val packets = input.readPackets().plus(listOf(divider1, divider2)).sorted()
        return (packets.indexOf(divider1) + 1) * (packets.indexOf(divider2) + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput).also { println("part1 test: $it") } == 13)
    check(part2(testInput).also { println("part2 test: $it") } == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private sealed interface DataValue : Comparable<DataValue> {
    data class IntValue(val value: Int) : DataValue {
        override fun toString(): String = value.toString()
    }

    data class ListValue(val value: List<DataValue>) : DataValue {
        override fun toString(): String = value.toString()
    }

    fun IntValue.toList() = ListValue(listOf(this))

    override operator fun compareTo(that: DataValue): Int {
        when {
            this is IntValue && that is IntValue -> {
                return this.value.compareTo(that.value)
            }

            this is ListValue && that is ListValue -> {
                repeat(minOf(this.value.size, that.value.size)) { i ->
                    val comp = this.value[i].compareTo(that.value[i])
                    if (comp != 0) {
                        return comp
                    }
                }
                return this.value.size.compareTo(that.value.size)
            }

            else -> when {
                this is IntValue -> return this.toList().compareTo(that)
                that is IntValue -> return this.compareTo(that.toList())
            }
        }
        error("something wrong")
    }

    companion object {
        fun parse(input: String): DataValue {
            val list = input.substring(1, input.lastIndex)
            return ListValue(
                if (list.isEmpty()) {
                    emptyList()
                } else {
                    buildList {
                        var index = 0
                        while (index < list.length) {
                            if (list[index] == '[') {
                                val parenthesis = Stack<Unit>()
                                parenthesis.push(Unit)
                                var p = index + 1
                                while (parenthesis.isNotEmpty()) {
                                    if (list[p] == '[') {
                                        parenthesis.push(Unit)
                                    }
                                    if (list[p] == ']') {
                                        parenthesis.pop()
                                    }
                                    p++
                                }
                                add(parse(list.substring(index, p)))
                                index = p + 1
                            } else {
                                var nextIndex = list.indexOf(',', startIndex = index + 1)
                                if (nextIndex == -1) {
                                    nextIndex = list.lastIndex + 1
                                }
                                add(IntValue(list.substring(index, nextIndex).toInt()))
                                index = nextIndex + 1
                            }
                        }
                    }
                }
            )
        }
    }
}
