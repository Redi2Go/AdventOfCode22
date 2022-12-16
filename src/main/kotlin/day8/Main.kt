package day8

import java.io.File
import kotlin.math.abs

fun main() {
    Day8(File("src/main/resources/input8.txt")).run()
}

data class Day8(val inputFile: File) : Runnable {
    val directions = listOf(
        Pair(1, 0),
        Pair(-1, 0),
        Pair(0, 1),
        Pair(0, -1)
    )

    override fun run() {
        val size = inputFile.readLines().size
        val trees = inputFile
            .readText()
            .filter { it.isDigit() }
            .mapIndexed { index, c ->
                Pair(index % size, index / size) to Integer.parseInt(c.toString())
            }
            .toMap()

        val visibleTrees = trees
            .filter {
                directions.any { dir ->
                    distance(trees, it.key, it.value, dir) <= 0
                }
            }

        val bestViewTree = visibleTrees
            .entries
            .maxOf {
                    val score = directions.map {dir ->
                        abs(distance(trees, it.key, it.value, dir))
                    }
                    .reduce { acc, distance ->
                        acc * distance
                    }
                    println("$score $it")

                return@maxOf score
                }

        println(bestViewTree)
        // println(visibleTrees)
    }

    fun distance(
        trees: Map<Pair<Int, Int>, Int>,
        xy: Pair<Int, Int>, z: Int,
        dir: Pair<Int, Int>
    ): Int {
        var distance = 0
        var newPos = xy
        while (true) {
            newPos = Pair(newPos.first + dir.first, newPos.second + dir.second)
            distance += 1

            val newZ = trees[newPos]
            if (newZ == null)
                return -distance + 1
            else if (newZ >= z)
                return distance
        }
    }
}