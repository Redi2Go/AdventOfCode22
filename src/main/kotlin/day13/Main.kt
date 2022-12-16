package day13

import org.json.simple.parser.JSONParser
import java.io.File
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

        val correctOrderPairs = packetPairs.filter { packetPair ->
            packetPair.first.zip(packetPair.second).forEach { // not all items checked
                val result = PacketComparable().compare(it.first, it.second)
                if (result == PacketComparable.UNORDERED)
                    return@filter false
                else if (result == PacketComparable.ORDERED)
                    return@filter true
            }

            return@filter true
        }

        println(correctOrderPairs)
    }

    class PacketComparable : Comparator<Any> {
        companion object {
            val ORDERED = 1
            val UNORDERED = -1
            val EQUAL = 0
        }

        @Suppress("UNCHECKED_CAST")
        override fun compare(o1: Any?, o2: Any?): Int {
            if (o1 is Long && o2 is Long)
                return compareIntegers(o1.toInt(), o2.toInt())

            var l1 = o1
            var l2 = o2
            if (o1 is Long)
                l1 = listOf(o1)
            else if (o2 is Long)
                l2 = listOf(o2)

            return compareLists(l1 as List<Long>, l2 as List<Long>)
        }

        private fun compareIntegers(int1: Int, int2: Int): Int {
            return int2.compareTo(int1)
        }

        private fun compareLists(list1: List<Long>, list2: List<Long>): Int {
            (0 until max(list1.size, list2.size)).forEach {i ->
                if (i >= list1.size)
                    return UNORDERED
                else if (i >= list2.size)
                    return ORDERED

                if (list1[i] < list2[i])
                    return ORDERED
                else if (list1[i] > list2[i])
                    return UNORDERED
            }

            return EQUAL
        }
    }
}
