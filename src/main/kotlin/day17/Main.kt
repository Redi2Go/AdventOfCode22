package day17

import java.io.File

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

        val playField = PlayField(mutableListOf())
        repeat(7) {
            playField.pieces += Pair(it, -1)
        }

        val jetPattern = inputFile
            .readText()
            .map {
                if (it == '>')
                    1
                else
                    -1
            }
            .toMutableList()

        repeat(2022) {
            simulatePiece(playField, rocks[it % rocks.size], jetPattern)
        }

        println(playField.pieces.maxOf { it.second })

        println(playField)
    }

    fun simulatePiece(playField: PlayField, rock: Rock, jetPattern: MutableList<Int>) {
        val spawnY = playField.pieces.maxOf { it.second } + 4
        val spawnX = 2

        var fallingRock = Rock(
            rock.pieces.toMutableList()
                .map { Pair(it.first + spawnX, it.second + spawnY) }
        )

        while (canFall(playField, fallingRock)) {
            val horizontal =
                if (jetPattern.isEmpty())
                    0
                else if (fallingRock.pieces.none {
                    it.first + jetPattern[0] >= 7 || it.first + jetPattern[0] < 0
                })
                    jetPattern[0]
                else
                    0

            println(horizontal)

            fallingRock = Rock(fallingRock.pieces.map { Pair(it.first + horizontal, it.second - 1) })
            if (jetPattern.isNotEmpty())
                jetPattern.removeAt(0)
        }

        val horizontal =
            if (jetPattern.isEmpty())
                0
            else if (fallingRock.pieces.none {
                    it.first + jetPattern[0] >= 7 || it.first + jetPattern[0] < 0
                })
                jetPattern[0]
            else
                0

        println(horizontal)

        fallingRock = Rock(fallingRock.pieces.map { Pair(it.first + horizontal, it.second) })
        if (jetPattern.isNotEmpty())
            jetPattern.removeAt(0)

        playField.pieces += fallingRock.pieces
    }

    fun canFall(playField: PlayField, rock: Rock): Boolean {
        rock.pieces.forEach { rockPiece ->
            playField.pieces.forEach {
                if (rockPiece.first == it.first && rockPiece.second - 1 == it.second)
                    return false
            }
        }

        return true
    }

    data class PlayField(val pieces: MutableList<Pair<Int, Int>>)

    data class Rock(val pieces: List<Pair<Int, Int>>)
}
