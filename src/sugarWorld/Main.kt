package sugarWorld

fun main(args : Array<String>) {
    val world = World(printLogs = false)

    for(t in 1..1000) {
        world.step(t)
        println("")
    }

    world.writeDataToCsv(world.gdp, "results/gdp.csv")
    world.writeDataToCsv(world.governmentBalance, "results/government_balance.csv")
    world.writeDataToCsv(world.industryBalance, "results/industry_balance.csv")
    world.writeDataToCsv(world.workforceBalance, "results/workforce_balance.csv")
    world.writeDataToCsv(world.industryStock, "results/industry_stock.csv")
}
