package day12

import java.io.File
import java.lang.IllegalStateException

fun main() {
    Day12(File("src/main/resources/input12.txt")).run()
}

data class Day12(val inputFile: File) : Runnable {
    companion object {
        val START_CHAR = 'S' - 'a'
        val END_CHAR = 'E' - 'a'
        val DIRECTIONS = setOf(
            Vec2(1, 0),
            Vec2(-1, 0),
            Vec2(0, 1),
            Vec2(0, -1),
        )
    }

    override fun run() {
        val size = inputFile.readLines().first().length

        var startPos: Vec2? = null

        var cells = inputFile
            .readText()
            .filter(Character::isLetter)
            .mapIndexed { index, c ->
                val cell = Cell(Vec2(index % size, index / size),
                when (c) {
                    'S' -> 'a' - 'a'
                    'E' -> 'z' - 'a'
                    else -> c - 'a'
                },
                if (c == 'E') 0 else Int.MAX_VALUE)

                if (c == 'S')
                    startPos = cell.pos

                return@mapIndexed cell
            }

//        part1(cells, startPos!!)
        part2(cells)
    }

    fun part2(cells0: List<Cell>) {
        var cells = cells0

        while (cells.none { it.h == 0 && it.endDistance != Int.MAX_VALUE }) {
            if ((Math.random() * 100).toInt() == 1) {
                printCellField(cells)
                println()
            }

            cells = cells.map { floodCell(cells, it) }
        }

        println()
        println(cells.first { it.h == 0 && it.endDistance != Int.MAX_VALUE }.endDistance)
    }

    fun part1(cells0: List<Cell>, startPos: Vec2) {
        var cells = cells0

        while (cells.first { it.pos == startPos }.endDistance == Int.MAX_VALUE) {
            if ((Math.random() * 100).toInt() == 1) {
                printCellField(cells)
                println()
            }

            cells = cells.map { floodCell(cells, it) }
        }

        println()
        println(cells.first { it.pos == startPos }.endDistance)
    }

    fun floodCell(cells: List<Cell>, cell: Cell): Cell {
        if (cell.endDistance != Int.MAX_VALUE)
            return cell

        val possibleNeighbourCells = DIRECTIONS
            .mapNotNull { dir ->
                cells.firstOrNull {
                    it.pos == cell.pos + dir
                }
            }
            .filter {
                cell.h >= it.h - 1
            }

        if (possibleNeighbourCells.isEmpty())
            return Cell(cell.pos, cell.h, Int.MAX_VALUE - 1)

        val minDistance = possibleNeighbourCells.minOf { it.endDistance }
        if (minDistance < Int.MAX_VALUE)
            return Cell(cell.pos, cell.h, minDistance + 1)

        return cell
    }

    fun printCellField(cells: List<Cell>) {
        val minX = cells.minOf { it.pos.x }
        val minY = cells.minOf { it.pos.y }
        val maxX = cells.maxOf { it.pos.x }
        val maxY = cells.maxOf { it.pos.y }

        (minY .. maxY).forEach { y ->
            (minX .. maxX).forEach { x ->
                val distance = cells.first { it.pos == Vec2(x, y) }.endDistance
                print(
                    if (distance == Int.MAX_VALUE) '*'
                    else if (distance == Int.MAX_VALUE - 1) 'X'
                    else 'a' + distance
                )
            }
            println()
        }
    }

    data class Cell(val pos: Vec2, val h: Int, val endDistance: Int)

    data class Vec2(val x: Int, val y: Int) {
        operator fun plus(other: Vec2): Vec2 {
            return Vec2(x + other.x, y + other.y)
        }

        operator fun minus(other: Vec2): Vec2 {
            return Vec2(x - other.x, y - other.y)
        }
    }
}
