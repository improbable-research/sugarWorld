package sugarWorld

import kotlin.math.max
import kotlin.math.min

class Government(var country: Country) : Consumer(bankBalance = 1000.0) {

    val noTimestepsAheadToForecast = 1
    var GDP = 0.0
    var previousGDP = GDP
    var desiredGDP = 1.0 * country.population
    var desiredReserves = 1000.0

    fun step(t: Int) {
        log("Step $t:")

        GDP = country.workforce.mostRecentWages
        var forecastedGDP = calculateForecastedGDP()
        previousGDP = GDP

        // Decide the extent to which to stimulate the economy through spending
        val amountToBuy = calculateAmountToBuy(forecastedGDP)
        val govSpending = country.industry.sellSugar(amountToBuy)
        bankBalance -= govSpending

        // Set the tax rate to cover that stimulus
        val taxRate = calculateTaxRate(govSpending, forecastedGDP)
        val taxAmount = GDP * taxRate

        log("Tax rate $taxRate, amount $taxAmount, GDP $GDP, expected GDP $forecastedGDP, gov spending $govSpending")

        // Ask for tax
        val taxesCollected = country.workforce.giveTaxes(GDP * taxRate)
        bankBalance += taxesCollected
    }


    fun calculateTaxRate(govSpending: Double, expectedGDP: Double): Double {
        var desiredGovSurplus = 0.1 * (desiredReserves - bankBalance)
        return Math.max((govSpending + desiredGovSurplus) / expectedGDP, 0.0)
    }

    fun calculateAmountToBuy(expectedGDP: Double): Double {
        return max(min(desiredGDP - expectedGDP, bankBalance), 0.0)
    }

    fun calculateForecastedGDP(): Double {
        val gdpChange = GDP - previousGDP
        val forecastGDPChange = noTimestepsAheadToForecast * gdpChange
        return GDP + forecastGDPChange
    }

    private fun log(msg: String) {
        if (country.printLogs) {
            println("[GOV${country.id}] $msg")
        }
    }
}
