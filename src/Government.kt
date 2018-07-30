import java.lang.Math.exp

class Government(var country : Country) : Consumer() {

    var perCapitaGDP = 0.0
    var previousPerCapitaGDP = 0.0
    var desiredPerCapitaGDP = 1.0
    var noTimestepsAheadToForecast = 1

    fun step () {
        // Compute change in GDP
        perCapitaGDP = calculateGDPperCapita()
        val changeInGDP = perCapitaGDP - previousPerCapitaGDP
        previousPerCapitaGDP = perCapitaGDP
        var expectedGDPperCapita = perCapitaGDP + (noTimestepsAheadToForecast * changeInGDP)

        // Decide the extent to which to stimulate the economy through spending
        val amountToBuy = calculateAmountToBuy(changeInGDP)
        country.industry.sellSugar(amountToBuy)

        // Set the tax rate to cover that stimulus
        var taxRate = calculateTaxPercentage(amountToBuy, expectedGDPperCapita)

        // Ask for tax
        country.workforce.giveTaxes(perCapitaGDP * country.population * taxRate)
    }

    fun calculateTaxPercentage (amountToFund: Double, expectedGDPperCapita: Double) : Double {
        return amountToFund + 0.1*(bankBalance-1000.0) / (expectedGDPperCapita * country.population)
    }

    fun calculateAmountToBuy (expectedGDPperCapita: Double) : Double {
        return (desiredPerCapitaGDP - expectedGDPperCapita) * country.population
    }


    fun calculateGDPperCapita () : Double {
        var priceOfSugar = 1.0
        return (country.industry.salesThisStep * priceOfSugar) / country.population
    }


}