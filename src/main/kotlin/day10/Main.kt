package day10

import java.io.File
import kotlin.math.abs

fun main() {
    Day10(File("src/main/resources/input10.txt")).run()
}

data class Day10(private val inputFile: File) : Runnable {
    override fun run() {
        var xRegister = 1
        val instructions = inputFile
            .readLines()
            .map {
                readInstruction(it)
            }

        val instructionsPerCycle = instructions
            .map {
                val registerValues = List(it.cycleCount) {
                    xRegister
                }

                xRegister += it.increment

                return@map registerValues
            }
            .flatten()

        // part1(instructionsPerCycle)
        part2(instructionsPerCycle)
    }

    private fun part2(instructionsPerCycle: List<Int>) {
        instructionsPerCycle
            .mapIndexed { index, value ->
                if (pixelBright(value, (index % 40))) '#' else ' '
            }.windowed(40, 40, false)
            .forEach {
                println(String(it.toCharArray()))
            }
    }

    private fun part1(instructionsPerCycle: List<Int>) {
        val strengthSum = IntRange(0, 5)
            .sumOf {
                (instructionsPerCycle[it * 40 + 20 - 1]) * (it * 40 + 20)
            }

        println(strengthSum)
    }

    private fun pixelBright(registerValue: Int, beamPosition: Int): Boolean {
        return abs(registerValue - beamPosition) <= 1
    }

    private fun readInstruction(string: String): Instruction {
        val split = string.split(" ")
        return if (split[0] == "noop")
            Instruction(0, 1)
        else if (split[0] == "addx")
            Instruction(split[1].toInt(), 2)
        else
            Instruction(0, 0)
    }

    data class Instruction(val increment: Int, val cycleCount: Int)
}
