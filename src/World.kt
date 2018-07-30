import java.util.*

class World {
    val countries = arrayOf(
            Country(12, this),
            Country(24, this),
            Country(20, this),
            Country(18, this)
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
        transport.step()
    }

    fun getPropensityToTradeWithSelf(country : Country) : Double {
        val i = countries.indexOf(country)
        return propensityToTrade[i][i]
    }

    fun sellSugar(amnt : Double, buyersCountry: Country) : Double {
        val buyer = buyersCountry.industry
        val i = countries.indexOf(buyersCountry)
        var totalBought = 0.0
        for((j, p) in propensityToTrade[i].withIndex()) {
            totalBought += countries[j].industry.exportSugar(amnt*p, buyer)
        }
        return totalBought
    }
}