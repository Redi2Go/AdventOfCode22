package day18

import java.io.File

fun main() {
    Day18(File("src/main/resources/input18.txt")).run()
}

data class Day18(val inputFile: File) : Runnable {
    val directions = listOf(
        Vec3(1, 0, 0),
        Vec3(-1, 0, 0),
        Vec3(0, 1, 0),
        Vec3(0, -1, 0),
        Vec3(0, 0, 1),
        Vec3(0, 0, -1),
    )

    override fun run() {
        val blocks = inputFile
            .readLines()
            .map { line ->
                val c = line.split(",").map { it.toInt() }

                return@map Vec3(c[0], c[1], c[2])
            }

        part2(blocks)
    }

    fun part2(blocks: List<Vec3>) {
        val bb = AABB(
            blocks.minOf { it.x },
            blocks.minOf { it.y },
            blocks.minOf { it.z },
            blocks.maxOf { it.x },
            blocks.maxOf { it.y },
            blocks.maxOf { it.z }
        )

        val sidesExposed = blocks.sumOf { block ->
            directions.count { direction ->
                escape(bb, blocks, block + direction, mutableSetOf()) != null
            }
        }

        println(sidesExposed)
    }

    fun escape(bb: AABB, blocks: List<Vec3>, pos: Vec3, visited: MutableSet<Vec3>): Vec3? {
        if (visited.contains(pos) || blocks.contains(pos))
            return null

        if (!bb.isInside(pos))
            return pos

        directions.forEach {
            visited.add(pos)

            val escaped = escape(bb, blocks, pos + it, visited)
            if (escaped != null)
                return escaped
        }

        return null
    }

    fun part1(blocks: List<Vec3>) {
        val exposedFaces = blocks.sumOf { block ->
            directions.count { side ->
                !blocks.contains(block + side)
            }
        }

        println(exposedFaces)
    }

    data class Vec3(val x: Int, val y: Int, val z: Int) {
        operator fun plus(other: Vec3): Vec3 {
            return Vec3(x + other.x, y + other.y, z + other.z)
        }
    }

    data class AABB(val x1: Int, val y1: Int, val z1: Int, val x2: Int, val y2: Int, val z2: Int) {
        fun isInside(pos: Vec3): Boolean {
            return pos.x in x1..x2 && pos.y in y1 .. y2 && pos.z in z1 .. z2
        }
    }
}
