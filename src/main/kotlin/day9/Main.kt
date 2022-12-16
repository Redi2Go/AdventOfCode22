package day9

import java.io.File
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main() {
    Day9(File("src/main/resources/input9.txt")).run()
}

data class Day9(val inputFile: File) : Runnable {
    val directions = mapOf(
        'R' to Vec2(1, 0),
        'L' to Vec2(-1, 0),
        'U' to Vec2(0, 1),
        'D' to Vec2(0, -1),
        'W' to Vec2(1, 1),
        'X' to Vec2(-1, 1),
        'Y' to Vec2(1, -1),
        'Z' to Vec2(-1, -1)
    )

    override fun run() {
        val rope = MutableList(10) { Vec2(0, 0) }
        val visited = mutableSetOf(rope[0])

        inputFile
            .readLines()
            .map {
                it.split(" ")
            }
            .map { line ->
                List(Integer.parseInt(line[1])) { directions[line[0][0]] }
            }
            .flatten()
            .filterNotNull()
            .forEach {
                println("Move $it")
                // printField(visited, rope)

                rope[0] += it
                for (i in 1 until rope.size)
                    rope[i] = follow(rope[i], rope[i - 1])

                visited.add(rope[rope.size - 1])
            }

        printField(visited, rope)
    }

    fun follow(current: Vec2, next: Vec2): Vec2 {
        if (distance(next, current).toInt() <= 1)
            return current

        val minMove = directions
            .values
            .minWithOrNull(compareBy { dir ->
                distance(next, current + dir)
            })!!

        return current + minMove
    }

    fun printField(visited: Set<Vec2>, rope: List<Vec2>) {
         val minX = visited.minOf { it.x }
         val minY = visited.minOf { it.y }
         val maxX = visited.maxOf { it.x }
         val maxY = visited.maxOf { it.y }
//        val minX = -10
//        val minY = -15
//        val maxX = 10
//        val maxY = 20

        val dy = max(maxY - minY, 1)

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val pos = Vec2(x, maxY - y + minY)
                val c = rope
                    .mapIndexed { index, it ->
                        Pair(it, index.toString()[0])
                    }.firstOrNull {
                        it.first.x == pos.x && it.first.y == pos.y
                    }

                print(
                    if (c != null)
                        c.second
                    else if (visited.contains(pos))
                        '#'
                    else
                        ' '
                )
            }
            println()
        }
    }

    fun distance(head: Vec2, tail: Vec2): Float {
        return sqrt((head.x - tail.x).toFloat().pow(2) + (head.y - tail.y).toFloat().pow(2))
    }

    data class Vec2(var x: Int, var y: Int) {
        operator fun plus(other: Vec2): Vec2 {
            return Vec2(x + other.x, y + other.y)
        }
    }
}