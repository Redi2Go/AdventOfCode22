package day6

import java.io.File

fun main() {
    Day6(File("src/main/resources/input6.txt")).run()
}

data class Day6(private val inputFile: File) : Runnable {
    override fun run() {
        part2()
    }

    fun part1() {
        val result = inputFile
            .readText()
            .windowed(4) {
                it.toSet().size == 4
            }
            .indexOf(true) + 4

        println(result)
    }

    fun part2() {
        val result = inputFile
            .readText()
            .windowed(14) {
                it.toSet().size == 14
            }
            .indexOf(true) + 14

        println(result)
    }
}
