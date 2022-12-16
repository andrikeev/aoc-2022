fun main() {
    fun part1(input: List<String>): Int {
        val grid = ValvesGrid.parse(input)
        var states = listOf(ValvesSingleOperatorState(me = Operator(listOf("AA"))))
        repeat(30) {
            states = states
                .asSequence()
                .map { state ->
                    with(state) {
                        val opened = me.opened
                        val newPressure = totalPressure + opened.values.sum()
                        val current = me.steps.first()
                        val nextSteps = me.steps.drop(1)
                        val valve = grid[current]
                        if (nextSteps.isEmpty() && !opened.contains(current) && valve.rate > 0) {
                            listOf(
                                copy(
                                    me = me.copy(opened = opened.plus(current to valve.rate)),
                                    totalPressure = newPressure,
                                )
                            )
                        } else {
                            if (nextSteps.isNotEmpty()) {
                                listOf(
                                    copy(
                                        me = me.copy(steps = nextSteps),
                                        totalPressure = newPressure,
                                    )
                                )
                            } else {
                                grid.nexSteps(current, opened.keys).map { steps ->
                                    copy(
                                        me = me.copy(steps = steps),
                                        totalPressure = newPressure,
                                    )
                                }
                            }
                        }.ifEmpty { listOf(copy(totalPressure = newPressure)) }
                    }

                }
                .flatten()
                .sortedByDescending(ValvesSingleOperatorState::totalPressure)
                .toList()
        }
        return states.maxBy { it.totalPressure }.also { println(it.me.opened) }.totalPressure
    }

    fun part2(input: List<String>): Int {
        val grid = ValvesGrid.parse(input)
        var states = listOf(
            ValvesTwoOperatorsState(
                me = Operator(listOf("AA")),
                el = Operator(listOf("AA")),
            )
        )
        repeat(26) {
            states = states
                .asSequence()
                .map { state ->
                    with(state) {
                        val opened = me.opened.plus(el.opened)
                        val currentTargets = listOf(me.steps.last(), el.steps.last())
                        val newPressure = totalPressure + opened.values.sum()
                        fun newOperators(operator: Operator): List<Operator> {
                            val current = operator.steps.first()
                            val nextSteps = operator.steps.drop(1)
                            val valve = grid[current]
                            return if (nextSteps.isEmpty() && !opened.contains(current) && valve.rate > 0) {
                                listOf(operator.copy(opened = operator.opened.plus(current to valve.rate)))
                            } else {
                                if (nextSteps.isNotEmpty()) {
                                    listOf(operator.copy(steps = nextSteps))
                                } else {
                                    grid.nexSteps(current, opened.keys.plus(currentTargets))
                                        .map { steps -> operator.copy(steps = steps) }
                                }
                            }
                        }
                        newOperators(me).map { me ->
                            newOperators(el).mapNotNull { el ->
                                if (me.steps != el.steps) {
                                    ValvesTwoOperatorsState(
                                        me = me,
                                        el = el,
                                        totalPressure = newPressure,
                                    )
                                } else {
                                    null
                                }
                            }
                        }
                            .flatten()
                            .ifEmpty { listOf(state.copy(totalPressure = newPressure)) }
                    }
                }
                .flatten()
                .sortedByDescending(ValvesTwoOperatorsState::totalPressure)
                .take(10000)
                .toList()
        }
        return states.maxBy { it.totalPressure }.also { println(it) }.totalPressure
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput).also { println("part1 test: $it") } == 1651)
    check(part2(testInput).also { println("part2 test: $it") } == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}

private data class Operator(
    val steps: List<String>,
    val opened: Map<String, Int> = emptyMap(),
)

private data class Valve(
    val rate: Int,
    val connections: List<String>,
)

private data class ValvesSingleOperatorState(
    val me: Operator,
    val totalPressure: Int = 0,
)

private data class ValvesTwoOperatorsState(
    val me: Operator,
    val el: Operator,
    val totalPressure: Int = 0,
)

private class ValvesGrid(
    val valves: Map<String, Valve>,
) {
    private val steps: MutableMap<String, MutableMap<String, List<String>>> = mutableMapOf()

    operator fun get(key: String) = valves.getValue(key)

    fun nexSteps(from: String, visited: Set<String> = emptySet()): List<List<String>> {
        return valves
            .filterKeys { !visited.contains(it) }
            .filterValues { it.rate > 0 }
            .entries
            .sortedByDescending { it.value.rate }
            .map { stepsFromToCached(from, it.key) }
            .sortedBy { it.size }
            .filter { it.isNotEmpty() }
    }

    private fun stepsFromToCached(from: String, to: String, visited: Set<String> = emptySet()): List<String> {
        return steps.getOrPut(from) { mutableMapOf() }.getOrPut(to) { stepsFromTo(from, to, visited) }
    }

    private fun stepsFromTo(from: String, to: String, visited: Set<String> = emptySet()): List<String> {
        val (_, connected) = get(from)
        return if (connected.contains(to)) {
            listOf(to)
        } else {
            connected.filterNot(visited::contains)
                .map { label -> label to stepsFromTo(label, to, visited + from) }
                .filter { it.second.isNotEmpty() }
                .minByOrNull { it.second.size }
                ?.let { listOf(it.first) + it.second }
                ?: emptyList()
        }
    }

    companion object {
        private val regexValve = Regex("Valve ([A-Z][A-Z]) has flow rate=(\\d+);")
        private val regexConnected = Regex("tunnels? leads? to valves? (.*)$")
        fun parse(input: List<String>): ValvesGrid {
            return ValvesGrid(
                buildMap {
                    input.map { line ->
                        val (label, rate) = regexValve.find(line)!!.groupValues.drop(1)
                        val connected = regexConnected.find(line)!!.groupValues[1].split(", ")
                        put(label, Valve(rate.toInt(), connected))
                    }
                }
            )
        }
    }
}
