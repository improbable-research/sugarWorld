package newSugarWorld

import newSugarWorld.Functions.logistic
import kotlin.math.max
import kotlin.math.min


class Workforce(val country: Country, var bankBalance: Double) {

    val minConsumption = country.population * 0.25
    var latestTax = country.government.taxRate
    var taxChange = 0.0

    var latestWages = bankBalance
    var wageChange = 0.0

    var prevBankBalance = bankBalance
    var proportionToSpend = 0.5


    fun step(t: Int) {
        val bankBalanceChange = bankBalance - prevBankBalance
        prevBankBalance = bankBalance

        proportionToSpend = logistic(bankBalanceChange * 0.1, 0.0, 0.5)

        val amountToBuy = max(minConsumption, (bankBalance + bankBalanceChange) * proportionToSpend)
        val bought = country.industry.sellProduct(amountToBuy)
        bankBalance -= bought
        log("Buying $amountToBuy ($proportionToSpend), bought $bought")
    }

    fun payTaxes(rate: Double): Double {
        val tax = rate * latestWages
        taxChange = tax - latestTax
        latestTax = tax

        bankBalance -= tax
        return tax
    }

    fun employLabour(units: Double, wageRate: Double): Double {
        val labourProvided = min(units, country.population)
        val wages = wageRate * labourProvided
        wageChange = wages - latestWages
        latestWages = wages
        bankBalance += wages
        return labourProvided
    }

    private fun log(msg: String) {
        println("[WRK ${country.id}] $msg")
    }
}
