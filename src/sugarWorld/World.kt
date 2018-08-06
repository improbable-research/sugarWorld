package sugarWorld

class World(val printLogs: Boolean = false) {
    val countries = arrayOf(
            Country(1, 12000, this, printLogs),
            Country(2, 12000, this, printLogs)
//            sugarWorld.Country(1, 12000000, this, printLogs),
//            sugarWorld.Country(2, 24000000, this, printLogs),
//            sugarWorld.Country(3, 20000000, this, printLogs),
//            sugarWorld.Country(4, 18000000, this, printLogs)
    )

    val propensityToTrade = arrayOf(
            arrayOf(0.5, 0.5),
            arrayOf(0.5, 0.5)
//            arrayOf(0.5, 0.5, 0.0, 0.0),
//            arrayOf(0.0, 0.5, 0.5, 0.0),
//            arrayOf(0.0, 0.0, 0.5, 0.5),
//            arrayOf(0.5, 0.0, 0.0, 0.5)
    )

    val transport = Transport()

    fun step(t: Int) {
        countries.forEach { it.step(t) }
        printStatus()
        transport.step(t)
    }

    fun getPropensityToTradeWithSelf(country: Country): Double {
        val i = countries.indexOf(country)
        return propensityToTrade[i][i]
    }

    fun sellSugar(amnt: Double, buyersCountry: Country): Double {
        val buyer = buyersCountry.industry
        val i = countries.indexOf(buyersCountry)
        var totalBought = 0.0
        for ((j, p) in propensityToTrade[i].withIndex()) {
            totalBought += countries[j].industry.exportSugar(amnt * p, buyer)
        }
        return totalBought
    }

    private fun printStatus() {
        countries.forEach { it.printStatus() }
        transport.printStatus()
    }
}
