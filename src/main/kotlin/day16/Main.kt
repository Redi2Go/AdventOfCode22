package day16

import java.io.File

fun main() {
    Day16(File("src/main/resources/input16.txt")).run()
}

data class Day16(val inputFile: File) : Runnable {
    override fun run() {
        data class SValve(val name: String, val rate: Int, val edges: List<String>)

        val sValves = inputFile
            .readLines()
            .filter { it.isNotBlank() }
            .map { it.split(" ") }
            .map { words ->
                val name = words[1]
                val rate = words[4].filter { it.isDigit() }.toInt()
                val edgeNames = words.subList(9, words.size).map { it.replace(",", "").trim() }

                return@map SValve(name, rate, edgeNames)
            }

        val valvesMap = sValves
            .map {
                Valve(it.name, it.rate)
            }

        val valves = resolveCrossings(
            sValveMap.values
                .filter { it.rate != 0 }
                .map { sValve ->
                    Valve(sValve.name, sValve.rate, sValve.edges.map {
                        Edge(1, sValve.name, it)
                    })
                }
        )

        println (valves)
    }

    fun resolveCrossings(valves: List<Valve>): List<Valve> {
        val valvesMap = valves.associateBy { it.name }

        return valves
            .filter { it.rate != 0 }
            .map {
                val valveEdges = it.edges.map {

                }

                Valve(it.name, it.rate, valveEdges)
            }
    }

    fun createValveEdges(valve: SValve, valves: Map<String, SValve>): List<Edge> {
        val edges = valve.edges
            .map { valves[it] }
            .partition { }
    }

    fun getCrossingEdges(valve: Valve, crossing: SValve): Edge {


    }

    data class Valve(val name: String, val rate: Int, val edges: List<Edge>);

    data class Edge(val travelTime: Int, var to: Edge)
}
