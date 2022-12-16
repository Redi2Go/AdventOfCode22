package day11

import java.io.File
import java.math.BigInteger

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

        repeat(10000) {_ ->
            monkeys.forEach { monkey ->
                monkey.items.forEach {
                    val newWorryLevel = monkey.operation(it)
                    val destination = monkey.destinations[if (newWorryLevel % monkey.divisibleTest == 0) 0 else 1]

                    monkeys[destination].items.add(newWorryLevel)
                    monkey.inspections++
                }
                monkey.items.clear()
            }

            monkeys.forEach {
                println(it.items)
            }

            println();
        }

        val mostInspections = monkeys.sortedByDescending { it.inspections }
        println(mostInspections[0].inspections * mostInspections[1].inspections)
    }

    fun parseMonkey(lines: List<String>): Monkey {
        return Monkey(
            lines[1].split(": ")[1].split(", ").map(Integer::parseInt).toMutableList(),
            parseOperation(lines[2]),
            lines[3].split(" by ")[1].toInt(),
            lines.subList(4, 6).map {
                it.split("monkey ")[1].toInt()
            }
        )
    }

    fun parseOperation(line: String): (Int) -> Int {
        val components = line.split(" = ")[1].split(" ")

        return { old ->
            val comp1 = evaluate(components[0], old)
            val comp2 = evaluate(components[2], old)

            if (components[1] == "*") comp1 * comp2 else comp1 + comp2
        }
    }

    fun evaluate(value: String, old: Int): Int {
        return if (value == "old") old else value.toInt()
    }

    fun factorize(value: BigInteger): FactorisableNumber {
        val factors = mutableListOf<Int>()
        var value0 = BigInteger(value.toByteArray())
        var i = BigInteger.valueOf(2)
        while (value0 != BigInteger.ONE) {
            if (value0 % i == BigInteger.ZERO) {
                factors.add(i.toInt())
                value0 /= i
                i = BigInteger.valueOf(1)
            }
            i++
        }

        return FactorisableNumber(factors)
    }

    data class Monkey(
        val items: MutableList<Int>,
        val operation: (Int) -> Int,
        val divisibleTest: Int,
        val destinations: List<Int>,
        var inspections: Int = 0
    )

    data class FactorisableNumber(val factors: List<Int>) {
        fun multiply(number: BigInteger) {

        }

        fun factorisableBy(prime: Int): Boolean {
            return factors.contains(prime)
        }

        fun toBigInt(): BigInteger {
            return factors.map { BigInteger.valueOf(it) }.redu
        }
    }
}