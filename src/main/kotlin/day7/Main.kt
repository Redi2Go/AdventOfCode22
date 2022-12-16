package day7

import java.io.File

fun main() {
    Day7(File("src/main/resources/input7.txt")).run()
}

data class Day7(val inputFile: File) : Runnable {
    override fun run() {
        val root = CFile("dir /", null)
        var current = root
        val lines = inputFile.readLines()
        lines
            .filterIndexed { index, _ ->
                index != 0
            }
            .forEachIndexed { index, s ->
                if (s.startsWith("$")) {
                    val args = s.split(" ")
                    if (args[1] == "cd") {
                        current = if (args[2] != "..")
                            current.getFile(args[2])
                        else
                            current.parent!!
                    }
                } else {
                    current.children.add(CFile(s, current))
                }
            }

        // part1(root)
        part2(root)
    }

    fun part2(root: CFile) {
        val spaceToFree = 30000000 - (70000000 - root.getTotalSize())
        val deleteFile = root
            .getAllDirectories()
            .filter {
                it.size == null && it.getTotalSize() >= spaceToFree
            }
            .minBy {
                it.getTotalSize()
            }

        println(deleteFile.getTotalSize())
    }

    fun part1(root: CFile) {
        val sumSize = root
            .getAllDirectories()
            .filter {
                it.size == null && it.getTotalSize() <= 100_000
            }
            .sumOf {
                it.getTotalSize()
            }

        println(sumSize)
    }

    class CFile() {
        var name: String = ""
        var size: Int? = 0
        val children: MutableList<CFile> = mutableListOf()
        var parent: CFile? = null

        constructor(input: String, parent: CFile?) : this() {
            val split = input.split(" ")
            this.size = split[0].toIntOrNull()
            this.name = split[1]
            this.parent = parent
        }

        fun getFile(name: String): CFile {
            return children.filter {
                it.name == name
            }.first()
        }

        fun getTotalSize(): Int {
            return if (size != null)
                size!!
            else
                children.sumOf { it.getTotalSize() }
        }

        fun getAllDirectories(): List<CFile> {
            return children + children.map { it.getAllDirectories() }.flatten()
        }

        override fun toString(): String {
            return (if (size == null)  "dir" else size.toString()) + " " + name + " " + children.toString()
        }
    }
}