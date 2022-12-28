package day17

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

fun main() {
    Day17(File("src/main/resources/input17.txt")).run()
}

data class Day17(val inputFile: File) : Runnable {
    override fun run() {
        val rocksRaw = "####\n\n.#.\n###\n.#.\n\n..#\n..#\n###\n\n#\n#\n#\n#\n\n##\n##"

        val rocks = rocksRaw
            .split("\n\n")
            .map { rockString ->
                rockString
                    .split("\n")
                    .mapIndexed { y, line ->
                        line.mapIndexed { x, c ->
                            if (c == '#')
                                Pair(x, y)
                            else
                                null
                        }
                            .filterNotNull()
                    }
                    .flatten()
            }
            .map {
                val maxY = it.maxOf { it.second }

                return@map it.map { Pair(it.first, maxY - it.second) }
            }
            .map { Rock(it) }

        val jetPattern = inputFile
            .readText()
            .trim()
            .map {
                if (it == '>')
                    1
                else
                    -1
            }

        val cycle = findCycle(jetPattern, rocks)

        val playField = PlayField(rocks = rocks, jetPattern = jetPattern)
        repeat((cycle.rockOffset + cycle.getCalculateOffset(1000000000000)).toInt()) {
            simulateNextPiece(playField)
        }

        val cycleLength = cycle.calculateHeight(1000000000000)
        val postCycle = playField.pieces.maxOf { it.second } - cycle.heightOffset

        println(cycleLength + postCycle + 1)

//        val playField = PlayField(rocks = rocks, jetPattern = jetPattern)
//        repeat(2022) {
//            simulateNextPiece(playField)
//        }
//
//        println(playField.pieces.maxOf { it.second } + 1)
    }

    fun findCycle(jetPattern: List<Int>, rocks: List<Rock>): Cycle {
        val semiCycles = mutableListOf<Cycle>()
        val playField = PlayField(rocks = rocks, jetPattern = jetPattern)

        while(true) {
            simulateNextPiece(playField)

            val semiCycle = semiCycles.firstOrNull { cycle -> cycle.streamIndex == playField.streamIndex.get() % jetPattern.size }
            val newHeightOffset = playField.pieces.maxOf { it.second }
            if (semiCycle != null) {
//                println("Found SemiCycle!")

                if (semiCycle.rockPeriod != -1) {
                    if (semiCycle.breaksCycle(playField.rockId, newHeightOffset)) {
                        semiCycles.remove(semiCycle)
                    } else {
                        semiCycle.successfulCycles++

//                        println("Doesnt break cycle ${semiCycle.successfulCycles} times!")

                        if (semiCycle.successfulCycles == 4)
                            return semiCycle
                    }
                } else {
                    semiCycle.rockPeriod = playField.rockId - semiCycle.rockOffset
                    semiCycle.heightPeriod = newHeightOffset - semiCycle.heightOffset
                }
            } else {
                semiCycles += Cycle(playField.rockId, newHeightOffset, -1, -1, playField.streamIndex.get() % jetPattern.size, 0)

//                semiCycles += Cycle(rockOffset, -1, playField.patternIndex.get() % jetPattern.size, 0)
            }
        }
    }

    /*fun isFullCycle(jetPattern: List<Int>, rocks: List<Rock>, semiCycle: Cycle): Boolean {
        val playField = PlayField(rocks = rocks, jetPattern = jetPattern)

        repeat(2 * semiCycle.rockId) {
            simulateNextPiece(playField)
        }

        if (playField.patternIndex.get() % jetPattern.size == semiCycle.streamIndex)
            println()

        return playField.patternIndex.get() % playField.jetPattern.size == semiCycle.streamIndex &&
                playField.pieces.maxOf { it.second } / 31 == semiCycle.offset
    }*/

    fun simulateNextPiece(playField: PlayField) {
        fun canMove(playField: PlayField, rock: Rock, movement: Pair<Int, Int>): Boolean {
            rock.pieces.forEach { rockPiece ->
                playField.pieces.forEach {
                    if (rockPiece.second + movement.second == it.second &&
                        rockPiece.first + movement.first == it.first
                    )
                        return false
                }
            }

            return true
        }

        fun moveByGravity(fallingRock: Rock): Rock {
            return Rock(fallingRock.pieces.map { Pair(it.first, it.second - 1) })
        }

        fun moveBySteam(fallingRock: Rock, playField: PlayField): Rock {
            val jetPattern = playField.jetPattern
            val patternIndex = playField.streamIndex

            val pattern = jetPattern[patternIndex.get() % jetPattern.size]
            patternIndex.incrementAndGet()

            val horizontal =
                if (jetPattern.isEmpty())
                    0
                else if (fallingRock.pieces.none {
                        it.first + pattern >= 7 || it.first + pattern < 0
                    } && canMove(playField, fallingRock, Pair(pattern, 0)))
                    pattern
                else
                    0

            return Rock(fallingRock.pieces.map { Pair(it.first + horizontal, it.second) })
        }

        fun printPlayField(playField: PlayField, fallingRock: Rock) {
            if (true)
                return

            val maxY = max(playField.pieces.maxOf { it.second }, fallingRock.pieces.maxOf { it.second })

            repeat(maxY + 1) { y ->
                repeat(9) { x ->
                    val pos = Pair(x - 1, maxY - y)

                    print(
                        if (playField.pieces.contains(pos))
                            '#'
                        else if (fallingRock.pieces.contains(pos))
                            '@'
                        else if (x == 0 || x == 8)
                            '|'
                        else
                            '.'
                    )
                }
                println()
            }

            println("+-------+")
            println()
        }

        val rock = playField.rocks[playField.rockId++ % playField.rocks.size]

        val spawnY = playField.pieces.maxOf { it.second } + 4
        val spawnX = 2

        var fallingRock = Rock(
            rock.pieces.toMutableList()
                .map { Pair(it.first + spawnX, it.second + spawnY) }
        )

        printPlayField(playField, fallingRock)

        fallingRock = moveBySteam(fallingRock, playField)

        while (canMove(playField, fallingRock, Pair(0, -1))) {
            fallingRock = moveByGravity(fallingRock)
            printPlayField(playField, fallingRock)
            fallingRock = moveBySteam(fallingRock, playField)

            printPlayField(playField, fallingRock)
        }

        printPlayField(playField, fallingRock)

        playField.pieces += fallingRock.pieces
    }

    data class Cycle(val rockOffset: Int, val heightOffset: Int, var rockPeriod: Int, var heightPeriod: Int, val streamIndex: Int, var successfulCycles: Int) {
        fun breaksCycle(newRockOffset: Int, newHeightOffset: Int): Boolean {
            return (newRockOffset - rockOffset) % rockPeriod != 0 || (newHeightOffset - heightOffset) % heightPeriod != 0
        }

        fun getCalculateOffset(rockId: Long): Long {
            return (rockId - rockOffset) % rockPeriod
        }

        fun calculateHeight(rockId: Long): Long {
            val n = (rockId - rockOffset) / rockPeriod

            return n * heightPeriod + heightOffset
        }
    }

    class PlayField(
        val pieces: MutableList<Pair<Int, Int>> = (0 until 7).map { Pair(it, -1) }.toMutableList(),
        val streamIndex: AtomicInteger = AtomicInteger(0),
        var rockId: Int = 0,
        val rocks: List<Rock>,
        val jetPattern: List<Int>
    )

    data class Rock(val pieces: List<Pair<Int, Int>>)
}
