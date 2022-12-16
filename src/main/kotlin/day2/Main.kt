package day2

import java.io.File

fun main() {
    Day2(File("src/resources/input3.txt")).run()
}

class Day2(private val inputFile: File) : Runnable {

    override fun run() {
        val plays = inputFile
            .readText()
            .split("\n")
            .filter(String::isNotBlank)
            .map { playLine ->
                val playChars = playLine.split(" ").map { it[0] }

                val opponentItem = Item.getItemByChar(playChars[0])

                val outcome = Outcome.getOutcomeByChar(playChars[1])

                return@map Item.values()
                    .map { Play(it, opponentItem) }
                    .first { it.getOutcome() == outcome }
            }

        val scoreSum = plays.sumOf { it.selfItem.score + it.getOutcome().score }

        plays.forEach { println(it) }
        println(scoreSum)
    }

    data class Play(val selfItem: Item, val opponentItem: Item) {
        fun getOutcome(): Outcome {
            return if (selfItem.beats == opponentItem)
                Outcome.WIN
            else if (selfItem == opponentItem)
                Outcome.DRAW
            else
                Outcome.LOSE
        }
    }

    enum class Outcome(val char: Char, val score: Int) {
        LOSE('X', 0),
        DRAW('Y', 3),
        WIN('Z', 6);

        companion object {
            fun getOutcomeByChar(char: Char): Outcome {
                return values().first { it.char == char }
            }
        }
    }

    enum class Item(val selfChar: Char, val opponentChar: Char, val score: Int) {
        ROCK('A', 'X', 1),
        PAPER('B', 'Y', 2),
        SCISSOR('C', 'Z', 3);

        val beats: Item
            get() = when(this) {
                ROCK -> SCISSOR
                PAPER -> ROCK
                SCISSOR -> PAPER
            }

        companion object {
            fun getItemByChar(char: Char): Item {
                return values().first { it.selfChar == char || it.opponentChar == char }
            }
        }
    }
}
