import java.lang.Math.exp

class Government(var country : Country) : Consumer() {

    var perCapitaGDP = 0.0
    var previousPerCapitaGDP = 0.0
    var desiredPerCapitaGDP = 1.0

    fun step () {
        // Compute change in GDP
        perCapitaGDP = calculateGDPperCapita()
        val changeInGDP = perCapitaGDP - previousPerCapitaGDP
        previousPerCapitaGDP = perCapitaGDP

        // Decide the extent to which to stimulate the economy through spending
        val amountToBuy = calculateAmountToBuy(changeInGDP)
        country.industry.sellSugar(amountToBuy)
    }

    fun calculateTaxPercentage (grossIncome: Double) : Double {
        var minTax = 0.1
        var maxTax = 0.6
        var topRateThreshold = 2.0
        var slopeOfTransition = 1.0
        var transitionLocation = 0.5
        return topRateThreshold *
                 (minTax * exp(transitionLocation * slopeOfTransition) + maxTax * exp(slopeOfTransition * grossIncome)) /
                 (Math.exp(transitionLocation*slopeOfTransition) + exp(slopeOfTransition * grossIncome))
    }

    fun calculateAmountToBuy (changeInGDP: Double) : Double {
        var noTimestepsAheadToForecast = 1
        var expectedGDPperCapita = perCapitaGDP + (noTimestepsAheadToForecast * changeInGDP)
        return (desiredPerCapitaGDP - expectedGDPperCapita) * country.population
    }


    fun calculateGDPperCapita () : Double {
        var priceOfSugar = 1.0
        return (country.industry.sales * priceOfSugar) / country.population
    }


}