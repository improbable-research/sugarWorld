package newSugarWorld

import newSugarWorld.Functions.logistic
import kotlin.math.max
import kotlin.math.min


class Government(val country: Country, var bankBalance: Double) {

    var taxRate = 0.25
    var industryStimulationRate = 0.5

    fun step(t: Int) {
        val gdpChangeRate = calculateGDPChangeRate()
        val changeEffect = logistic(gdpChangeRate, 1.0, 0.5)

//        taxRate = max(0.0,taxRate - changeEffect * 0.05)
//        industryStimulationRate = max(0.0,industryStimulationRate + changeEffect * 0.2)

//        taxRate = max(0.0, taxRate - gdpChangeRate)
//        industryStimulationRate = min(max(0.0,industryStimulationRate + gdpChangeRate), 1.0)
        log("GDP change rate $gdpChangeRate, change effect $changeEffect, tax rate $taxRate, ind. stim. rate $industryStimulationRate")

        bankBalance += country.workforce.payTaxes(taxRate)

        val productToBuy = max(0.0, bankBalance * industryStimulationRate)
        val productBought = country.industry.sellProduct(productToBuy)
        bankBalance -= productBought

        log("Wanted to buy $productToBuy, bought $productBought")
    }

    private fun calculateGDPChangeRate(): Double {
        val gdpRecord = country.industry.salesRecord
        val prevT1 = gdpRecord[gdpRecord.size - 1]
        val prevT2 = gdpRecord.getOrElse(gdpRecord.size - 2, { i -> 0.0 })
        return (prevT1 - prevT2) / prevT1
    }

    private fun log(msg: String) {
        println("[GOV ${country.id}] $msg")
    }
}
