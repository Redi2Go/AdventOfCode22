package day15

import day15.Day15.Window.Companion.RESOLUTION
import java.io.File
import java.lang.Integer.min
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max

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
//        val minX = 0
//        val minY = 0
        val maxX = min(sensors.maxOf { it.pos.x }, 4000000)
        val maxY = min(sensors.maxOf { it.pos.y }, 4000000)

        val window = Window(
            maxX / 2f,
            maxY / 2f,
            max(maxX, maxY)
        )
        printRaster(window.rasterize(sensors))
    }

    fun printRaster(raster: Raster) {
        fun sigmoidChar(value: Int): Char {
            val sigmoid = 1.0 / (1.0 + exp(-(0.000001 * value)))

            return 'a' + (sigmoid * ('z' - 'a')).toInt()
        }

        var min = Integer.MAX_VALUE
        var minX = 0
        var minY = 0
        raster.forEachIndexed { y, line ->
            print("%03d ".format(y))

            line.forEachIndexed { x, it ->
                if (it < min) {
                    min = it
                    minY = y
                    minX = x
                }

                print(if (it != 0) "${sigmoidChar(it)}" else ".")
            }
            println()
        }

        println("min at $minX, $minY valued $min")
    }

    data class Window(
        private val centerCellX: Float,
        private val centerCellY: Float,
        val cellsPerWindow: Float
    ) {

        companion object {
            const val RESOLUTION = 100f
        }

        private val minX = (centerCellX - cellsPerWindow / 2f).toInt()
        private val minY = (centerCellY - cellsPerWindow / 2f).toInt()

        private val maxX = (centerCellX + cellsPerWindow / 2f).toInt()
        private val maxY = (centerCellY + cellsPerWindow / 2f).toInt()

        private val step = (cellsPerWindow / RESOLUTION).toInt()

        fun rasterize(sensors: List<Sensor>): Raster {
            return (minY..maxY step step).map { y ->
                (minX..maxX step step).map { x ->
                    sensors.sumOf {
                        if (it.inRange(Vec2(x, y)))
                            it.range() - (it.pos - Vec2(x, y)).manhattanLength() + 1
                        else
                            0
                    }
                }
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

        fun manhattanLength(): Int {
            return abs(x) + abs(y)
        }
    }
}