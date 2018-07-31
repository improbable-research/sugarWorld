import kotlin.math.min

class Government(var country: Country) : Consumer() {

    var GDP = country.population.toDouble()
    var previousGDP = GDP
    var desiredGDP = 1.0 * country.population
    var desiredReserves = 1000.0

    fun step() {
        // Compute change in GDP
        GDP = country.workforce.mostRecentWages
        var expectedGDP = calculateExpectedGDP()
        previousGDP = GDP

        // Decide the extent to which to stimulate the economy through spending
        val amountToBuy = calculateAmountToBuy(expectedGDP)
        val govSpending = country.industry.sellSugar(amountToBuy)
        bankBalance -= govSpending

        // Set the tax rate to cover that stimulus
        var taxRate = calculateTaxPercentage(govSpending, expectedGDP)

        println("Gov tax rate $taxRate, GDP $GDP, gov spending $govSpending, expected GDP $expectedGDP")

        // Ask for tax
        val taxesCollected = country.workforce.giveTaxes(GDP * taxRate)
        bankBalance += taxesCollected
    }


    fun calculateTaxPercentage(govSpending: Double, expectedGDP: Double): Double {
        var desiredGovSurplus = 0.1 * (desiredReserves - bankBalance)
        return (govSpending + desiredGovSurplus) / expectedGDP
    }

    fun calculateAmountToBuy(expectedGDP: Double): Double {
        return min(desiredGDP - expectedGDP, bankBalance)
    }

    fun calculateExpectedGDP(): Double {
        var noTimestepsAheadToForecast = 1
        return GDP + noTimestepsAheadToForecast * (GDP - previousGDP)
    }
}
