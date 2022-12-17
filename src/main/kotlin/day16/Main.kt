package day16

import java.io.File
import java.lang.IllegalArgumentException

fun main() {
    Day16(File("src/main/resources/input16.txt")).run()
}

// is bidirectional graph?
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

        val valves = sValves.associate {
            it.name to Valve(it.name, it.rate, listOf())
        }

        valves.values.zip(sValves).forEach { pair ->
            pair.first.edges = pair.second.edges.map { sValve ->
                Edge(1, valves[sValve]!!)
            }
        }

        val workingValves = valves.filter { it.value.working() }

        resolveCrossings(valves)

        val startModel = Model(
            0,
            workingValves.values.toSet(),
            mutableSetOf(),
            valves["AA"]!!,
            listOf(),
            0
        )

        startModel.doAction(startModel.createMoveAction(valves["DD"]!!))
        startModel.doAction(startModel.createOpenAction())
        startModel.doAction(startModel.createMoveAction(valves["CC"]!!)) // s
        startModel.doAction(startModel.createMoveAction(valves["BB"]!!))
        startModel.doAction(startModel.createOpenAction())
//        startModel.doAction(startModel.createMoveAction(valves["AA"]!!))
//        startModel.doAction(startModel.createMoveAction(valves["II"]!!))
        startModel.doAction(startModel.createMoveAction(valves["JJ"]!!))
        startModel.doAction(startModel.createOpenAction())
//        startModel.doAction(startModel.createMoveAction(valves["II"]!!))
//        startModel.doAction(startModel.createMoveAction(valves["AA"]!!))
        startModel.doAction(startModel.createMoveAction(valves["DD"]!!)) // o
        startModel.doAction(startModel.createMoveAction(valves["EE"]!!)) // s
//        startModel.doAction(startModel.createMoveAction(valves["FF"]!!))
//        startModel.doAction(startModel.createMoveAction(valves["GG"]!!))
        startModel.doAction(startModel.createMoveAction(valves["HH"]!!))
        startModel.doAction(startModel.createOpenAction())
//        startModel.doAction(startModel.createMoveAction(valves["GG"]!!))
//        startModel.doAction(startModel.createMoveAction(valves["FF"]!!))
        startModel.doAction(startModel.createMoveAction(valves["EE"]!!))
        startModel.doAction(startModel.createOpenAction())
        startModel.doAction(startModel.createMoveAction(valves["DD"]!!)) // o
        startModel.doAction(startModel.createMoveAction(valves["CC"]!!))
        startModel.doAction(startModel.createOpenAction())

        startModel.skip()

        val bestMoves = bestMoves(startModel)
        println(bestMoves)
    }

    fun findRoute(start: Valve, to: Valve, visitedValves: MutableSet<Valve>): List<Valve> {
        visitedValves.add(start)

        start.edges.forEach {
            if (it.to == to)
                return listOf(it.to)

            val route = findRoute(it.to, to, visitedValves)
            if (route.isNotEmpty())
                return listOf(it.to) + route
        }

        return listOf()
    }

    var best = 0

    fun bestMoves(model: Model): Model {
        val possibleActions = model.getPossibleActions()
        if (possibleActions.isEmpty()) {
            model.skip()
        }

        if (model.minute >= 30) {
            if (model.releasedPressure > best) {
                best = model.releasedPressure
                println("New best $best")
            }

            return model
        }

        return possibleActions.map {
            val newModel = Model(
                model.releasedPressure,
                model.workingValves.toSet(),
                model.openValves.toMutableSet(),
                model.currentValve,
                model.actions.toList(),
                model.minute
            )

            newModel.doAction(it)

            return@map bestMoves(newModel)
        }.maxBy { it.releasedPressure }
    }

    class Model(
        var releasedPressure: Int,
        val workingValves: Set<Valve>,
        val openValves: MutableSet<Valve>,
        var currentValve: Valve,
        var actions: List<Action?>,
        var minute: Int,
    ) {
        var previous = currentValve

        fun doAction(action: Action) {
            releasePressure()

            if (action.type == 'M') {
                if (currentValve.edges.none { it.to == action.valve })
                    throw IllegalArgumentException()

                repeat(currentValve.edges.first { it.to == action.valve }.travelTime - 1) {
                    releasePressure()
                }

                previous = currentValve
                currentValve = action.valve
            } else if (action.type == 'O') {
                if (openValves.contains(currentValve))
                    throw IllegalArgumentException()

                openValves.add(currentValve)
            }

            actions += action
        }

        fun releasePressure() {
            minute++

            openValves.forEach {
                releasedPressure += it.rate
            }
        }

        fun skip() {
            repeat(30 - minute) {
                releasePressure()
            }
        }

        fun getPossibleActions(): List<Action> {
            if (openValves.containsAll(workingValves))
                return listOf()

            val possibilities =  currentValve.edges
                .map { Action('M', it.to) }
                .union(
                    if (currentValve.working() && !openValves.contains(currentValve))
                        listOf(Action('O', currentValve))
                    else
                        listOf()
                )
                .toList()

            val noReturn = possibilities.filter { it.type != 'M' || it.valve != previous }
            return (if (noReturn.isEmpty())
                possibilities
            else
                noReturn)
        }

        fun createMoveAction(to: Valve): Action {
            return Action('M', to)
        }

        fun createOpenAction(): Action {
            return Action('O', currentValve)
        }

        override fun toString(): String {
            return "Model[releasedPressure=$releasedPressure, openValves=${openValves.size}]"
        }
    }

    fun resolveCrossings(workingValves: Map<String, Valve>) {
        fun findWorkingValves(valve: Valve, visitedValves: MutableSet<Valve>, intend: Int): List<Edge> {
//        repeat(intend) { print('\t') }
//        println("Checking ${if (valve.working()) "working" else "broken"} valve ${valve.name} with edges ${valve.edges.map { it.to.name }}")

            visitedValves.add(valve)

            return valve.edges
                .filter { !visitedValves.contains(it.to) }
                .map { edge ->
                    if (edge.to.working()) {
//                        repeat(intend + 1) { print('\t') }
//                    println("Valve ${edge.to.name} is working!")
                        return@map listOf(edge)
                    }

                    return@map findWorkingValves(edge.to, visitedValves, intend + 1).map {
                        Edge(it.travelTime + 1, it.to)
                    }
                }
                .flatten()
        }

        workingValves.values.forEach {
            it.edges = findWorkingValves(it, mutableSetOf(), 0)
//            println()
        }
    }

    data class Action(val type: Char, val valve: Valve)

    data class Valve(val name: String, val rate: Int, var edges: List<Edge>) {
        fun working(): Boolean {
            return rate != 0
        }

        override fun hashCode(): Int {
            return name.hashCode() * rate.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Valve

            if (name != other.name) return false
            if (rate != other.rate) return false
            if (edges != other.edges) return false

            return true
        }

        override fun toString(): String {
            return "Valve[$name, $rate, ${edges.map { it.to.name }} "
        }
    }

    data class Edge(val travelTime: Int, val to: Valve)
}
