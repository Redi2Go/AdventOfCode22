package day4

import java.io.File
import java.util.*

fun main() {
    Day5(File("src/main/resources/input5win.txt")).run()
}

data class Day5(private val inputFile: File) : Runnable {
    override fun run() {
        part2()
    }

    fun part2() {
        val inputs = inputFile
            .readText()
            .replace("\r", "")
            .split("\n\n")
            .partition {
                it.contains("move")
            }
            .let {pair ->
                var horizontalTowers = pair.second[0]
                    .split("\n")
                    .filter {
                        it.contains(Regex("\\["))
                    }
                    .flatMap {
                        listOf(it.toCharArray())
                    }
                val maxLength = horizontalTowers.maxOf { it.size }
                horizontalTowers = horizontalTowers.map {
                    it + " ".repeat(maxLength - it.size).toCharArray()
                }

                val verticalTowers = horizontalTowers
                    .last()
                    .mapIndexed { index, c ->
                        return@mapIndexed if (!c.isLetter())
                            ';'
                        else
                            LinkedList(horizontalTowers
                                .filter {
                                    it[index] != ' '
                                }
                                .map {
                                    it[index]
                                })
                    }
                    .filterIsInstance<LinkedList<Char>>()

                val moves = pair.first[0].split("\n").filter(String::isNotBlank).map { moveString ->
                    val numbers = moveString.split(" ").filter { it.matches(Regex("\\d+")) }.map(Integer::parseInt)

                    return@map MultiMove(numbers[0], numbers[1], numbers[2])
                }

                return@let Pair(verticalTowers, moves)
            }

        val towers = inputs.first
        val moves = inputs.second

        println(moves)
        println(towers)

        moves.forEach {
            val crane = LinkedList<Char>()
            for (i in 0 until  it.count) {
                crane.add(towers[it.from - 1].pop())
            }

            crane.reversed().forEach { craneItem -> towers[it.to - 1].push(craneItem) }

            println(towers)
        }

        val result = towers
            .map { it.pop() + "" }
            .reduce { c1, c2 -> c1 + c2 }

        println(result)
    }

    fun part1() {
        val inputs = inputFile
            .readText()
            .replace("\r", "")
            .split("\n\n")
            .partition {
                it.contains("move")
            }
            .let {pair ->
                var horizontalTowers = pair.second[0]
                    .split("\n")
                    .filter {
                        it.contains(Regex("\\["))
                    }
                    .flatMap {
                        listOf(it.toCharArray())
                    }
                val maxLength = horizontalTowers.maxOf { it.size }
                horizontalTowers = horizontalTowers.map {
                    it + " ".repeat(maxLength - it.size).toCharArray()
                }

                val verticalTowers = horizontalTowers
                    .last()
                    .mapIndexed { index, c ->
                        return@mapIndexed if (!c.isLetter())
                            ';'
                        else
                            LinkedList(horizontalTowers
                                .filter {
                                    it[index] != ' '
                                }
                                .map {
                                    it[index]
                                })
                    }
                    .filterIsInstance<LinkedList<Char>>()

                val moves = pair.first[0].split("\n").filter(String::isNotBlank).map { moveString ->
                    val numbers = moveString.split(" ").filter { it.matches(Regex("\\d+")) }.map(Integer::parseInt)

                    return@map List(numbers[0]) { SingleMove(numbers[1], numbers[2]) }
                }

                return@let Pair(verticalTowers, moves.flatten())
            }

        val towers = inputs.first
        val moves = inputs.second

        println(moves)
        println(towers)

        moves.forEach {
            towers[it.to - 1].push(towers[it.from - 1].pop())
            println(towers)
        }

        val result = towers
            .map { it.pop() + "" }
            .reduce { c1, c2 -> c1 + c2 }

        println(result)
    }

    data class SingleMove(val from: Int, val to: Int)

    data class MultiMove(val count: Int, val from: Int, val to: Int)
}
