package day1

import java.io.File

fun main() {
    Day1(File("src/main/resources/input1.txt")).run()
}

data class Day1(val inputFile: File) : Runnable {
    override fun run() {
        val sum = inputFile
            .readText()
            .replace("\r", "")
            .split("\n\n")
            .map { elve ->
                elve
                    .split("\n")
                    .filter { it.isNotBlank() }
                    .sumOf {
                        it.trim().toInt()
                    }
            }
            .sortedDescending()
            .take(3)
            .sum()

        println(sum)
    }
}
