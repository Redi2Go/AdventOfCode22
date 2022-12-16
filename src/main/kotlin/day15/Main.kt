package day15

import java.io.File
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

fun main() {
    Day15(File("src/main/resources/input15.txt")).run()
}

typealias Raster = List<List<Int>>

data class Day15(val inputFile: File) : Runnable {
    override fun run() {
        val sensors = inputFile
            .readLines()
            .map { line ->
                line.split(" ").map { word ->
                    word.filter { it.isDigit() || it == '-' }
                }.filter {
                    it.isNotBlank()
                }.map {
                    it.toInt()
                }
            }
            .map { Sensor(Vec2(it[0], it[1]), Vec2(it[2], it[3])) }

//        part1(sensors)
        part2(sensors)
    }

    fun part2(sensors: List<Sensor>) {
        val maxX = min(sensors.maxOf { it.pos.x }, 4000000)
        val maxY = min(sensors.maxOf { it.pos.y }, 4000000)

        var min = Long.MAX_VALUE
        (0..maxX).forEach { x ->
            val function = { y: Long ->
                sensors.sumOf {
                    if (it.inRange(Vec2(x, y.toInt())))
                        it.range() - (it.pos - Vec2(x, y.toInt())).manhattanLength() + 1L
                    else
                        0L
                }
            }

            val minY = GradientAscend(
                function,
                { it: Long -> max(min(it, maxX.toLong()), 0L)},
                maxY / 2L
            ).descend()
            val value = function(x.toLong())
            if (value < min) {
                min = value
                println("New min $min at ${Vec2(x, minY.toInt())}")
            }

            if (value == 0L) {
                println("Beacon at ${Vec2(x, minY.toInt())}")
                return
            }
        }
    }

    data class GradientAscend(
        val function: (Long) -> Long,
        val clampFunction: (Long) -> Long,
        val initial: Long
    ) {
        fun descend(): Long {
            var x = initial
            for (i in 0 until 100) {
                val y = function(x)
                val gradient = finiteDifference(x)
                if (gradient == 0L)
                    return x

                x = clampFunction((x + gradient * 0.001f).toLong())
            }

            return x
        }

        private fun finiteDifference(at: Long): Long {
            return function(at + 1) - function(at)
        }
    }

    fun part1(sensors: List<Sensor>) {
        val beaconPositions = sensors.map { it.beaconPos }.toSet()

        val y = 10
        val minX = sensors.minOf { it.range() - it.pos.x }
        val maxX = sensors.maxOf { it.range() + it.pos.x }

        val inRangeCount = (minX..maxX).count { x ->
            !beaconPositions.contains(Vec2(x, y)) && beaconInRange(sensors, Vec2(x, y))
        }

        println(inRangeCount)

        printField(sensors)
    }

    fun beaconInRange(sensors: List<Sensor>, pos: Vec2): Boolean {
        return sensors.any { it.inRange(pos) }
    }

    fun printField(sensors: List<Sensor>) {
        val beaconPositions = sensors.map { it.beaconPos }.toSet()
        val sensorPositions = sensors.map { it.pos }
        val positions = sensorPositions.union(beaconPositions)

        val minX = 0
        val minY = 0
        val maxX = sensors.maxOf { it.pos.x }
        val maxY = sensors.maxOf { it.pos.y }

        for (y in minY..maxY) {
            print("%03d ".format(y))
            for (x in minX..maxX) {
                print(
                    if (beaconPositions.contains(Vec2(x, y)))
                        'B'
                    else if (sensorPositions.contains(Vec2(x, y)))
                        'S'
                    else if (beaconInRange(sensors, Vec2(x, y)))
                        '#'
                    else '.'
                )
            }
            println()
        }
    }

    data class Sensor(val pos: Vec2, val beaconPos: Vec2) {
        fun range(): Int {
            return (pos - beaconPos).manhattanLength()
        }

        fun inRange(pos: Vec2): Boolean {
            return (this.pos - pos).manhattanLength() <= range()
        }
    }

    data class Vec2(val x: Int, val y: Int) {

        constructor(comp: Int) : this(comp, comp)

        operator fun plus(other: Vec2): Vec2 {
            return Vec2(x + other.x, y + other.y)
        }

        operator fun minus(other: Vec2): Vec2 {
            return Vec2(x - other.x, y - other.y)
        }

        operator fun times(other: Int): Vec2 {
            return Vec2(other * x, other * y)
        }

        operator fun div(other: Int): Vec2 {
            return Vec2(x / other, y / other)
        }

        fun min(other: Vec2): Vec2 {
            return Vec2(min(x, other.x), min(y, other.y))
        }

        fun max(other: Vec2): Vec2 {
            return Vec2(max(x, other.x), max(y, other.y))
        }

        fun manhattanLength(): Int {
            return abs(x) + abs(y)
        }
    }
}
