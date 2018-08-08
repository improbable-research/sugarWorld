package sugarWorld

fun main(args : Array<String>) {
    val world = World(printLogs = false)

    for(t in 1..100) {
        world.step(t)
        println("")
    }

    world.writeGDPToCsv("results/gdp.csv")
}
