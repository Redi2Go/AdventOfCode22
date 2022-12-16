package day13

import org.json.simple.parser.JSONParser
import java.io.File
import java.util.SortedSet
import kotlin.math.max

fun main() {
    Day13(File("src/main/resources/input13.txt")).run()
}

data class Day13(val inputFile: File) : Runnable {
    override fun run() {
        val parser = JSONParser()

        val packetPairs = inputFile
            .readLines()
            .filter(String::isNotBlank)
            .map {
                parser.parse(it)
            }
            .chunked(2)
            .map { Pair(it[0] as List<*>, it[1] as List<*>) }

        val packets = packetPairs
            .mapIndexed { index, packetPair ->
                Pair(index + 1, packetPair)
            }

        part2(packets)
    }

    fun part1(packets: List<Pair<Int, Pair<List<*>, List<*>>>>) {
        val correctOrderPairs = packets.filter { packetPair ->
            PacketComparable().compare(packetPair.second.first, packetPair.second.second) != -1
        }

        println(correctOrderPairs)
        println(correctOrderPairs.sumOf { it.first })
    }

    fun part2(packetPairs: List<Pair<Int, Pair<List<*>, List<*>>>>) {
        val packets = packetPairs.flatMap { listOf(it.second.first, it.second.second) }

        val dividers = listOf(
            listOf(listOf(2.toLong())),
            listOf(listOf(6.toLong()))
        )

        val sortedPackets = packets
            .union(dividers)
            .toSortedSet(PacketComparable()::compare)
            .reversed()
            .mapIndexed { index, packet ->
                Pair(index + 1, packet)
            }

        sortedPackets.forEach {
            println(it)
        }

        val dividerIndices = sortedPackets.filter { dividers.contains(it.second) }.take(2).map { it.first }
        println(dividerIndices[0] * dividerIndices[1])
        }

    class PacketComparable : Comparator<Any> {
        companion object {
            val ORDERED = 1
            val UNORDERED = -1
            val EQUAL = 0
        }

        override fun compare(o1: Any?, o2: Any?): Int {
            if (o1 is Long && o2 is Long)
                return compareIntegers(o1.toInt(), o2.toInt())

            var l1 = o1
            var l2 = o2
            if (o1 is Long)
                l1 = listOf(o1)
            else if (o2 is Long)
                l2 = listOf(o2)

            return compareLists(l1 as List<*>, l2 as List<*>)
        }

        private fun compareIntegers(int1: Int, int2: Int): Int {
//            println("$int1 and $int2 are ${int2.compareTo(int1)}")
            return int2.compareTo(int1)
        }

        private fun compareLists(list1: List<*>, list2: List<*>): Int {
            (0 until max(list1.size, list2.size)).forEach {i ->
                if (i >= list2.size) {
//                    println("$list1 and $list2 are unordered")
                    return UNORDERED
                } else if (i >= list1.size) {
//                    println("$list1 and $list2 are ordered")
                    return ORDERED
                }

                val comparison = compare(list1[i], list2[i])
                if (comparison != EQUAL)
                    return comparison
            }

            return EQUAL
        }
    }
}
