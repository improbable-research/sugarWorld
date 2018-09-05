package sugarWorld

import kotlin.math.max
import kotlin.math.min

class Government(var country: Country) : Consumer(bankBalance = country.population.toDouble()) {

    val noTimestepsAheadToForecast = 1
    var GDP = 0.0
    var previousGDP = GDP
    var desiredGDP = 1.0 * country.population
    var desiredReserves = 1000.0

    fun step(t: Int) {
        log("Step $t:")

        GDP = country.industry.salesThisStep
        var forecastedGDP = calculateForecastedGDP()
        previousGDP = GDP

        val govSpending = stimulateEconomy(forecastedGDP)

        val taxRate = calculateTaxRateToCoverGovSpending(govSpending, forecastedGDP)
        val taxAmount = GDP * taxRate

        log("Tax rate $taxRate, amount $taxAmount, GDP $GDP, expected GDP $forecastedGDP, gov spending $govSpending")

        val taxesCollected = country.workforce.giveTaxes(GDP * taxRate)
        bankBalance += taxesCollected
    }

    private fun stimulateEconomy(forecastedGDP: Double): Double {
        val amountToBuy = calculateAmountToBuy(forecastedGDP)
        val govSpending = country.industry.sellSugar(amountToBuy)
        bankBalance -= govSpending
        return govSpending
    }

    private fun calculateTaxRateToCoverGovSpending(govSpending: Double, expectedGDP: Double): Double {
        var desiredGovSurplus = 0.1 * (desiredReserves - bankBalance)
        return min(max((govSpending + desiredGovSurplus) / expectedGDP, 0.0), 1.0)
    }

    private fun calculateAmountToBuy(expectedGDP: Double): Double {
        return max(min(desiredGDP - expectedGDP, bankBalance), 0.0)
    }

    private fun calculateForecastedGDP(): Double {
        val gdpChange = GDP - previousGDP
        val forecastGDPChange = noTimestepsAheadToForecast * gdpChange
        return max(GDP + forecastGDPChange, 0.0)
    }

    private fun log(msg: String) {
        if (country.printLogs) {
            println("[GOV${country.id}] $msg")
        }
    }
}
