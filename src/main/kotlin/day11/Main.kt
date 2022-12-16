package day11

import java.io.File

fun main() {
    Day11(File("src/main/resources/input11.txt")).run()
}

data class Day11(val inputFile: File) : Runnable {
    override fun run() {
        val monkeys = inputFile
            .readLines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .windowed(6, 6, false)
            .map(this::parseMonkey)

        repeat(10000) { i ->
            monkeys.forEach { monkey ->
                monkey.items.forEach {
                    val newWorryLevel = monkey.operation(it)
                    val destination = monkey.destinations[if (newWorryLevel.divisible(monkey.divisor)) 0 else 1]

                    monkeys[destination].items.add(newWorryLevel)
                    monkey.inspections++
                }
                monkey.items.clear()
            }

            println("$i: ${monkeys.map { it.inspections }}")
        }

        val mostInspections = monkeys.sortedByDescending { it.inspections }
        val product = mostInspections[0].inspections.toLong() * mostInspections[1].inspections
        println(product)

        if (product != 2713310158)
            throw IllegalStateException("Wrong")
     }

    fun parseMonkey(lines: List<String>): Monkey {
        return Monkey(
            lines[1].split(": ")[1].split(", ").map {
                ModuloNumber(it.toInt())
            }.toMutableList(),
            parseOperation(lines[2]),
            lines[3].split(" by ")[1].toInt(),
            lines.subList(4, 6).map {
                it.split("monkey ")[1].toInt()
            }
        )
    }

    fun parseOperation(line: String): (ModuloNumber) -> ModuloNumber {
        val components = line.split(" = ")[1].split(" ")

        return { old ->
            val comp1 = evaluate(components[0], old)
            val comp2 = evaluate(components[2], old)

            if (components[1] == "*") comp1.multiply(comp2) else comp1.add(comp2)
        }
    }

    fun evaluate(value: String, old: ModuloNumber): ModuloNumber {
        return if (value == "old") old else ModuloNumber(value.toInt())
    }

    data class Monkey(
        val items: MutableList<ModuloNumber>,
        val operation: (ModuloNumber) -> ModuloNumber,
        val divisor: Int,
        val destinations: List<Int>,
        var inspections: Int = 0
    )

    class ModuloNumber(
        private var numbers: List<Int> = listOf()
    ) {
        constructor(initial: Int) : this() {
            (2 .. 30).forEach {
                numbers += (initial % it)
            }
        }

        fun divisible(divisor: Int): Boolean {
            return numbers[divisor - 2] == 0
        }

        fun add(summand: ModuloNumber): ModuloNumber {
            numbers = numbers.mapIndexed { index, number ->
                (number + summand.numbers[index]) % (index + 2)
            }

            return this
        }

        fun multiply(factor: ModuloNumber): ModuloNumber {
            numbers = numbers.mapIndexed { index, number ->
                (number * factor.numbers[index]) % (index + 2)
            }

            return this
        }
    }
}
