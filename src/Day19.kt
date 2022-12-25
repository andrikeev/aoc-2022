fun main() {

    fun part1(input: List<String>): Int {
        val blueprints = input.map(Blueprint::parse)
        return blueprints.sumOf { blueprint ->
            (blueprint.id * blueprint.simulate(24)).also { println("${blueprint.id}: $it") }
        }
    }

    fun part2(input: List<String>): Int {
        val blueprints = input.map(Blueprint::parse).take(3)
        return blueprints
            .map { blueprint -> blueprint.simulate(32) }
            .reduceRight { left, right -> left * right }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput).also { println("part1 test: $it") } == 33)
    check(part2(testInput).also { println("part2 test: $it") } == 56 * 62)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

private fun Blueprint.simulate(minutes: Int): Int {
    var states = listOf(FabricState(minutesLeft = minutes))

    fun FabricState.canBuildGeodeRobot() = geodeRobotPrice.first <= ore && geodeRobotPrice.second <= obsidian

    fun FabricState.canBuildObsidianRobot() = obsidianRobotPrice.first <= ore && obsidianRobotPrice.second <= clay

    fun FabricState.canBuildClayRobot() = clayRobotPrice <= ore

    fun FabricState.canBuildOreRobot() = oreRobotPrice <= ore

    fun FabricState.shouldBuildRobots() = minutesLeft > 1

    fun FabricState.shouldBuildObsidianRobot() = shouldBuildRobots() && !canBuildGeodeRobot() && minutesLeft > 2

    fun FabricState.shouldBuildClayRobot(): Boolean {
        return shouldBuildRobots() &&
                !canBuildGeodeRobot() &&
                !canBuildObsidianRobot() &&
                (clayRobots < obsidianRobotPrice.second) &&
                minutesLeft > 3
    }

    fun FabricState.newState(
        ore: Int = this.ore + this.oreRobots,
        clay: Int = this.clay + this.clayRobots,
        obsidian: Int = this.obsidian + this.obsidianRobots,
        geode: Int = this.geode + this.geodeRobots,
        oreRobots: Int = this.oreRobots,
        clayRobots: Int = this.clayRobots,
        obsidianRobots: Int = this.obsidianRobots,
        geodeRobots: Int = this.geodeRobots,
    ): FabricState = copy(
        minutesLeft = minutesLeft - 1,
        ore = ore,
        clay = clay,
        obsidian = obsidian,
        geode = geode,
        oreRobots = oreRobots,
        clayRobots = clayRobots,
        obsidianRobots = obsidianRobots,
        geodeRobots = geodeRobots,
    )

    fun FabricState.shouldBuildOreRobot(): Boolean {
        return shouldBuildRobots() &&
                !canBuildGeodeRobot() &&
                !canBuildObsidianRobot() &&
                (oreRobots < maxOf(clayRobotPrice, obsidianRobotPrice.first, geodeRobotPrice.first))
    }

    repeat(minutes) {
        val newStates = mutableListOf<FabricState>()
        states.forEach { state ->
            with(state) {
                if (canBuildGeodeRobot()) {
                    newStates.add(
                        newState(
                            ore = ore + oreRobots - geodeRobotPrice.first,
                            obsidian = obsidian + obsidianRobots - geodeRobotPrice.second,
                            geodeRobots = geodeRobots + 1,
                        )
                    )
                }
                if (shouldBuildObsidianRobot() && canBuildObsidianRobot()) {
                    newStates.add(
                        newState(
                            ore = ore + oreRobots - obsidianRobotPrice.first,
                            clay = clay + clayRobots - obsidianRobotPrice.second,
                            obsidianRobots = obsidianRobots + 1,
                        )
                    )
                }
                if (shouldBuildClayRobot() && canBuildClayRobot()) {
                    newStates.add(
                        newState(
                            ore = ore + oreRobots - clayRobotPrice,
                            clayRobots = clayRobots + 1,
                        )
                    )
                }
                if (shouldBuildOreRobot() && canBuildOreRobot()) {
                    newStates.add(
                        newState(
                            ore = ore + oreRobots - oreRobotPrice,
                            oreRobots = oreRobots + 1,
                        )
                    )
                }
                newStates.add(
                    newState()
                )
            }
        }
        states = newStates.sortedByDescending(FabricState::geode).take(10000000)
    }

    return states.maxOf(FabricState::geode)
}

private data class FabricState(
    val minutesLeft: Int,
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
    val oreRobots: Int = 1,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0,
)

private class Blueprint(
    val id: Int,
    val oreRobotPrice: Int,
    val clayRobotPrice: Int,
    val obsidianRobotPrice: Pair<Int, Int>,
    val geodeRobotPrice: Pair<Int, Int>,
) {
    companion object {
        fun parse(input: String): Blueprint {
            val id = input.substringAfter("Blueprint ").substringBefore(":").toInt()
            val (ore, clay, obsidian, geode) = input.substringAfter(":").split(".")
            val (oreRobotPrice) = ore.split(" ").mapNotNull(String::toIntOrNull)
            val (clayRobotPrice) = clay.split(" ").mapNotNull(String::toIntOrNull)
            val (obsidianRobotPriceOre, obsidianRobotPriceClay) = obsidian.split(" ").mapNotNull(String::toIntOrNull)
            val (geodeRobotPriceOre, geodeRobotPriceObsidian) = geode.split(" ").mapNotNull(String::toIntOrNull)
            return Blueprint(
                id = id,
                oreRobotPrice = oreRobotPrice,
                clayRobotPrice = clayRobotPrice,
                obsidianRobotPrice = Pair(obsidianRobotPriceOre, obsidianRobotPriceClay),
                geodeRobotPrice = Pair(geodeRobotPriceOre, geodeRobotPriceObsidian),
            )
        }
    }
}
