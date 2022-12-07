fun main() {

    val totalSpace = 70_000_000
    val requiredSpace = 30_000_000

    fun buildFileTree(input: List<String>): Map<String, Dir> {
        val fileTree = mutableMapOf<String, Dir>()
        var currentDir = ""
        fun MutableMap<String, Dir>.currentDir(): Dir = getValue(currentDir)
        fun String.nestedDir(target: String) = when {
            this == "/" -> "$this$target"
            target == "/" -> target
            else -> "$this/$target"
        }
        input.forEach { line ->
            val args = line.split(" ")
            val firstArg = args[0]
            when {
                firstArg == "$" -> when (args[1]) {
                    "cd" -> {
                        val target = args[2]
                        if (target == "..") {
                            currentDir = fileTree.currentDir().parent
                        } else {
                            val nestedDir = currentDir.nestedDir(target)
                            fileTree[nestedDir] = Dir(parent = currentDir)
                            currentDir = nestedDir
                        }
                    }
                }

                firstArg == "dir" -> {
                    fileTree.currentDir().children.add(currentDir.nestedDir(args[1]))
                }

                firstArg.toIntOrNull() != null -> {
                    fileTree.currentDir().size += firstArg.toInt()
                }
            }
        }
        return fileTree
    }

    fun part1(input: List<String>): Int {
        val fileTree = buildFileTree(input)
        fun Dir.totalSize(): Int = size + children.map(fileTree::getValue).sumOf(Dir::totalSize)
        return fileTree.entries
            .map { it.value.totalSize() }
            .filter { it < 100_000 }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val fileTree = buildFileTree(input)
        fun Dir.totalSize(): Int = size + children.map(fileTree::getValue).sumOf(Dir::totalSize)
        val availableSpace = totalSpace - fileTree.getValue("/").totalSize()
        val spaceToFree = requiredSpace - availableSpace
        return fileTree.entries
            .map { it.value.totalSize() }
            .filter { it >= spaceToFree }
            .min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

private data class Dir(
    val parent: String,
    var size: Int = 0,
    val children: MutableList<String> = mutableListOf(),
)
