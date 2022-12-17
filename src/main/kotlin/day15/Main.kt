package day15

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day15(File("src/main/resources/input15.txt")).run()
}

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

        val steps = Vec2(maxX / 10, maxY / 10)

        var min = Integer.MAX_VALUE
        (0..maxY step steps.y).forEach { y ->
            (0..maxX step steps.x).forEach { x ->
                val function = { at: Vec2 ->
                    sensors.sumOf {
                        if (it.inRange(at))
                            it.range() - (it.pos - at).manhattanLength() + 1
                        else
                            0
                    }
                }

                val minPos = GradientDescend(
                    function,
                    { it: Vec2 -> it.x >= 0 && it.y >= 0 && it.x <= maxX && it.y <= maxY },
                    Vec2(x, y)
                ).descend()
                val value = function(minPos)
                if (value < min) {
                    min = value
                    println("New min $min at $minPos")
                }

                if (value == 0) {
                    println("Beacon at $minPos; frequency ${minPos.x * 4000000L + minPos.y}")
                    return
                }
            }
        }
    }

    data class GradientDescend(
        val function: (Vec2) -> Int,
        val isInBoundsFunction: (Vec2) -> Boolean,
        val initial: Vec2
    ) {
        companion object {
            val DIRECTIONS = listOf(
                Vec2(1, 1),
                Vec2(-1, 1),
                Vec2(1, -1),
                Vec2(-1, -1),
                Vec2(1, 0),
                Vec2(-1, 0),
                Vec2(0, 1),
                Vec2(0, -1)
            )
        }

        fun descend(): Vec2 {
            var x = initial

            while(true) {
                val y = function(x)

                x += DIRECTIONS.firstOrNull {
                    isInBoundsFunction(x + it) && function(x + it) < y
                } ?: return x
            }
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
