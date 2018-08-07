package sugarWorld

fun main(args : Array<String>) {
    val world = World(printLogs = true)

    for(t in 1..10) {
        world.step(t)
//        print("$t ")
//        for(country in world.countries) {
//            print("${country.industry.salesThisStep} ")
//        }
        println("")
    }

    world.writeGDPToCsv("results/gdp.csv")
}
