package sugarWorld

import sugarWorld.Functions.expCdf
import sugarWorld.Functions.logistic
import kotlin.math.max
import kotlin.math.min

class Workforce(val country: Country) : Consumer(bankBalance = country.population.toDouble()) {

    val baselineExpenditure = country.population.toDouble() * 0.0
    private var prevWagesInSugarPerDay = 1.0
    private var prevWages = 0.0
    private var wageChange = 0.0
    private var prevTaxes = 0.0
    private var taxChange = 0.0

    var mostRecentWages = country.population.toDouble()


    fun sellTime(days: Double, wageInSugarPerDay: Double): Double {
        val correctedWageInSugarPerDay = max(wageInSugarPerDay, 0.0)
        val wageChangeInSugarPerDay = correctedWageInSugarPerDay - prevWagesInSugarPerDay
        val employmentRate = calculateEmploymentRate(correctedWageInSugarPerDay, wageChangeInSugarPerDay)

        val daysOfWorkAvailable = country.population * employmentRate
        val daysOfWork = min(daysOfWorkAvailable, days)

        prevWages = mostRecentWages
        mostRecentWages = correctedWageInSugarPerDay * daysOfWork
        wageChange = mostRecentWages - prevWages
        bankBalance += mostRecentWages
        prevWagesInSugarPerDay = correctedWageInSugarPerDay

        log("Asked for $days days at $wageInSugarPerDay. Providing $daysOfWork for $mostRecentWages pay, bank balance $bankBalance.")

        return daysOfWork
    }

    fun giveTaxes(amnt: Double): Double {
        taxChange = amnt - prevTaxes
        val taxesGiven = Math.min(amnt, bankBalance)
        bankBalance -= taxesGiven
        log("Asked for $amnt taxes (prev $prevTaxes, change $taxChange). Gave $taxesGiven, bank balance $bankBalance.")
        prevTaxes = amnt
        return taxesGiven
    }

    fun step(t: Int) {
        log("Step $t:")
        val proportionToSave = calculateProportionToSave(taxChange, wageChange)
        val expectedAfterTaxBankBalance = bankBalance - prevTaxes
        val expenditureOnSugar = max(baselineExpenditure, expectedAfterTaxBankBalance * (1.0 - proportionToSave))
        country.industry.sellSugar(expenditureOnSugar)
        bankBalance -= expenditureOnSugar
        log("Saving proportion $proportionToSave, spending $expenditureOnSugar, bank balance $bankBalance")
    }

    private fun log(msg: String) {
        if (country.printLogs) {
            println("[WF${country.id}] $msg")
        }
    }

    companion object {

        private const val wageChangeK = 0.25
        private const val wageChangeMidpoint_x0 = -0.5
        private const val wageLambda = 2.0
        private const val wealthChangeK = 0.05
        private const val wealthChangeMidpoint_x0 = 0.0
        private const val wealthChangeEffectMultiplier = 2.0
        private const val savingLambda = 1.0

        fun calculateProportionToSave(taxChange: Double, wageChange: Double): Double {
            val wealthChange = wageChange - taxChange
            val wealthChangeEffect = logistic(
                    - wealthChange,
                    wealthChangeMidpoint_x0,
                    wealthChangeK
            ) * wealthChangeEffectMultiplier
            return expCdf(wealthChangeEffect, savingLambda)
        }
        fun calculateEmploymentRate(wageInSugarPerDay: Double, wageChangeInSugarPerDay: Double): Double {
            val absoluteWageEffect = expCdf(wageInSugarPerDay, wageLambda)
            val wageChangeEffect = logistic(wageChangeInSugarPerDay, wageChangeMidpoint_x0, wageChangeK)
            return absoluteWageEffect * wageChangeEffect
        }
    }
}
