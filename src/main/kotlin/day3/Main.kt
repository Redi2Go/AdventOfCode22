package day3

import java.io.File

fun main() {
    Day3(File("src/main/resources/input3.txt")).run()
}

data class Day3(private val inputFile: File) : Runnable {
    override fun run() {
        val rucksacks = inputFile
            .readText()
            .split("\n")
            .filter(String::isNotBlank)

        println(rucksacks.chunked(3).sumOf {
            getItemPriority(
                it[0].asIterable()
                    .intersect(it[1].asIterable())
                    .intersect(it[2].asIterable())
                    .first()
            )
        })
    }

    fun part1() {
        val rucksacks = inputFile
            .readText()
            .split("\n")
            .filter(String::isNotBlank)
            .map { Rucksack(it) }

        val priority = rucksacks.map { rucksack ->
            rucksack.compartments[0].items
                .intersect(rucksack.compartments[1].items.asIterable().toSet())
                .sumOf {
                    getItemPriority(it)
                }
        }.sum()

        rucksacks.forEach {
            println(it)
        }

        println(priority)
    }

    fun part2() {

    }

    fun getItemPriority(char: Char): Int {
        return if (char.isLowerCase())
            char - 'a' + 1
        else
            getItemPriority(char.lowercaseChar()) + 26
    }

    data class Compartment(val items: CharArray) {
        constructor(itemsString: String) : this(itemsString.toCharArray())
    }

    data class Rucksack(val compartments: List<Compartment>) {
        constructor(line: String) : this(
            line.chunked(line.length / 2).filter(String::isNotBlank).map { Compartment(it) }
        )
    }
}
