class World {
    val countries = arrayOf(
            Country(1, 12, this),
            Country(2, 24, this),
            Country(3, 20, this),
            Country(4, 18, this)
    )

    val propensityToTrade = arrayOf(
            arrayOf(0.5, 0.5, 0.0, 0.0),
            arrayOf(0.0, 0.5, 0.5, 0.0),
            arrayOf(0.0, 0.0, 0.5, 0.5),
            arrayOf(0.5, 0.0, 0.0, 0.5)
    )

    val transport = Transport()

    fun step() {
        countries.forEach { it.step() }
        printStatus()
        transport.step()
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
