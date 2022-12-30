package day19

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    Day19(File("src/main/resources/input19.txt")).run()
}

// complete garbage
data class Day19(val inputFile: File) : Runnable {
    override fun run() {
        val ores = listOf("ore", "clay", "obsidian", "geode")

        val blueprints = inputFile.readLines()
            .filter { it.isNotBlank() }
            .map { it.split(": ")[1].trim() }
            .map { it.split(". ").map { it.replace(".", "") } }
            .map { blueprintLine ->
                blueprintLine
                    .map { it.split(" ") }
                    .map { robotWords ->
                        val cost = mutableMapOf<String, Int>()
                        val costStrings = robotWords.subList(4, robotWords.size)
                        repeat(costStrings.size / 3 + 1) {
                            val index = 3 * it
                            cost[costStrings[index + 1]] = costStrings[index].toInt()
                        }

                        Robot(ores.indexOf(robotWords[1]), ores.map { cost[it] ?: 0 })
                    }
            }

        val playField = PlayField(blueprints[1])

        println(blueprints)

        blueprints.take(3).forEachIndexed { index, robots ->
            Thread {
                println("  " + bestScore(PlayField(robots), AtomicInteger(0)))
            }.start()
        }
    }

    fun bestScore(playField: PlayField, best: AtomicInteger): Int {
        if (playField.minute > 32) {
            if (playField.resources[3] > best.get()) {
                best.set(playField.resources[3])
                print("$best ")
            }

            return playField.resources[3]
        }

        if (playField.getTheoreticalBest() < best.get() || playField.noBuildCount > 2)
            return Integer.MIN_VALUE

        var max = Integer.MIN_VALUE
        for (robot in playField.bluePrint + null) {
            if (robot != null && (!playField.isNecessaryRobot(robot) || !playField.canAfford(robot)))
                continue

            val newPlayField = PlayField(playField)

            newPlayField.mine()

            if (robot != null) {
                newPlayField.buildRobot(robot)
                newPlayField.noBuildCount = 0
            } else {
                newPlayField.noBuildCount++
            }

            newPlayField.minute++

            val score = bestScore(newPlayField, best)
            if (score > max)
                max = score
        }

        return max
    }

    data class PlayField(
        val bluePrint: List<Robot>,
        val builtRobots: Array<Int>,
        val resources: Array<Int> = arrayOf(0, 0, 0, 0),
        var minute: Int = 1,
        var noBuildCount: Int = 0
    ) {
        constructor(bluePrint: List<Robot>) : this(
            bluePrint,
            arrayOf(1, 0, 0, 0)
        )

        constructor(playField: PlayField) : this(
            playField.bluePrint,
            playField.builtRobots.copyOf(),
            playField.resources.copyOf(),
            playField.minute
        )

        fun buildRobot(robot: Robot) {
            for (i in resources.indices)
                resources[i] -= robot.cost[i]

            builtRobots[robot.resource] += 1

//            builtRobots += robot
        }

        fun mine() {
            for (i in builtRobots.indices)
                resources[i] += builtRobots[i]
        }

        val geode = bluePrint[3]
        val ore = bluePrint[0]

        fun isNecessaryRobot(robot: Robot): Boolean {
            if (minute == 32)
                return false
            if (minute == 31 && robot != geode)
                return false
            if (minute == 30 && robot == ore)
                return false

            if (canAfford(geode) && robot != geode)
                return false

            return robot.resource !in resources || resources[robot.resource] < 64
//            return true
        }

//        fun getAffordableRobots(): List<Robot> {
//            return bluePrint.filter { canAfford(it) }
//        }

        fun canAfford(robot: Robot): Boolean {
            for (i in 0 until 4)
                if (robot.cost[i] > resources[i])
                    return false

            return true
        }

        fun getTheoreticalBest(): Int {
            val remaining = 32 - minute + 1

            return builtRobots[3] * remaining +
                    (remaining * (remaining - 1) / 2) +
                    resources[3]

        }
    }

    data class Robot(val resource: Int, val cost: List<Int>)
}
