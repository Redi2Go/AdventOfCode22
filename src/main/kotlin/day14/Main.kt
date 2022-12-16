package day14

import java.io.File

fun main() {
    Day14(File("src/main/resources/input14.txt")).run()
}

data class Day14(val inputFile: File) : Runnable {
    companion object {
        val SAND_MOVE_DIRECTIONS = listOf(
            Vec2(0, 1),
            Vec2(-1, 1),
            Vec2(1, 1)
        )
    }

    override fun run() {
        val strokes0 = inputFile
            .readLines()
            .map { line ->
                line
                    .split(" -> ")
                    .map { it.split(",").map(String::toInt) }
                    .map { Vec2(it[0], it[1]) }
            }
            .map {
                val stroke = mutableSetOf<Vec2>()
                for (i in 0 until it.size - 1) {
                    stroke += it[i].rangeTo(it[i + 1])
                }

                return@map stroke
            }
            .flatten()
            .map { it to Object(it, 'r') }
            .toMap()

        val strokes = HashMap(strokes0)

        printField(strokes)
//        part1(strokes)
        part2(strokes)
    }

    fun part1(strokes: MutableMap<Vec2, Object>) {
        while (simulateSandDrop(strokes, Vec2(500, 0))) {
//            printField(strokes)
        }

        println(strokes.count { it.value.type == 's' })
    }

    fun part2(strokes: MutableMap<Vec2, Object>) {
        val floorHeight = strokes.values.maxOf { it.pos.y } + 2
        repeat(4 * floorHeight) { i ->
            strokes += Object(Vec2(500 + i - 2 * floorHeight, floorHeight), 'r').let { it.pos to it }
        }

        while (simulateSandDrop(strokes, Vec2(500, 0))) {
//            printField(strokes)
        }

        printField(strokes)
        println(strokes.count { it.value.type == 's' })
    }

    fun simulateSandDrop(objects: MutableMap<Vec2, Object>, summonPos: Vec2): Boolean {
        val sand = Object(summonPos, 's')
        if (objects.containsKey(sand.pos))
            return false

        val void = objects.values.maxOf { it.pos.y }

        while (true) {
            if (sand.pos.y > void)
                return false

            val moveDirection = SAND_MOVE_DIRECTIONS
                .filter { dir ->
                    !objects.containsKey(sand.pos + dir)
                }
                .firstOrNull() ?: break

            sand.pos += moveDirection
        }

        objects += sand.pos to sand

        return true
    }

    fun printField(strokes: MutableMap<Vec2, Object>) {
        printField(strokes.values.toList())
    }

    fun printField(strokes: List<Object>) {
        val minX = strokes.minOf { it.pos.x }
        val minY = strokes.minOf { it.pos.y }
        val maxX = strokes.maxOf { it.pos.x }
        val maxY = strokes.maxOf { it.pos.y }

        for (y in minY .. maxY) {
            for (x in minX..maxX) {
                print(
                    if (strokes.contains(Object(Vec2(x, y), 'r')))
                        '#'
                    else if (strokes.contains(Object(Vec2(x, y), 's')))
                        '+'
                    else
                        '.'
                )
            }
            println()
        }
    }

    data class Object(var pos: Vec2, val type: Char)

    data class Vec2(val x: Int, val y: Int) {
        operator fun plus(other: Vec2): Vec2 {
            return Vec2(x + other.x, y + other.y)
        }

        operator fun minus(other: Vec2): Vec2 {
            return Vec2(x - other.x, y - other.y)
        }

        fun sign(): Vec2 {
            return Vec2(x.compareTo(0), y.compareTo(0))
        }

        fun rangeTo(other: Vec2): Set<Vec2> {
            val delta = (other - this).sign()
            val range = mutableSetOf(this)
            var last = this
            do {
                last += delta
                range += last
            } while (last != other)

            return range
        }
    }
}
