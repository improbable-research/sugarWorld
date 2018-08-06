package sugarWorld

import java.lang.Math.exp

class Workforce(val country: Country) : Consumer(bankBalance = 1000.0) {

    private var prevWagesInSugarPerDay = 1.0
    private var prevTaxes = 0.0
    private var taxChange = 0.0

    var mostRecentWages = country.population.toDouble()


    fun sellTime(days: Double, wageInSugarPerDay: Double): Double {
        val correctedWageInSugarPerDay = Math.max(wageInSugarPerDay, 0.0)
        val wageChangeInSugarPerDay = correctedWageInSugarPerDay - prevWagesInSugarPerDay
        val employmentRate = calculateEmploymentRate(correctedWageInSugarPerDay, wageChangeInSugarPerDay)

        val daysOfWorkAvailable = country.population * employmentRate
        val daysOfWork = Math.min(daysOfWorkAvailable, days)

        mostRecentWages = correctedWageInSugarPerDay * daysOfWork
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
        val proportionToSave = calculateProportionToSave(taxChange)
        val expectedAfterTaxBankBalance = bankBalance - prevTaxes
        val expenditureOnSugar = expectedAfterTaxBankBalance * (1.0 - proportionToSave)
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

        private val wageChangeK = 0.5
        private val wageChangeMidpoint_x0 = -0.5
        private val wageLambda = 2.0

        private val taxChangeK = 0.05
        private val taxChangeMidpoint_x0 = 0.0
        private val taxChangeEffectMultiplier = 2.0
        private val savingLambda = 1.0

        fun logisticFunction(x: Double, x0: Double, k: Double): Double {
            return 1.0 / (1.0 + exp(-k * (x - x0)))
        }

        fun expCdf(x: Double, lambda: Double): Double {
            return 1.0 - exp(-x * lambda)
        }

        fun calculateEmploymentRate(wageInSugarPerDay: Double, wageChangeInSugarPerDay: Double): Double {
            val absoluteWageEffect = expCdf(wageInSugarPerDay, wageLambda)
            val wageChangeEffect = logisticFunction(wageChangeInSugarPerDay, wageChangeMidpoint_x0, wageChangeK)
            return absoluteWageEffect * wageChangeEffect
        }

        fun calculateProportionToSave(taxChange: Double): Double {
            val taxChangeEffect = logisticFunction(taxChange, taxChangeMidpoint_x0, taxChangeK) * taxChangeEffectMultiplier
            return expCdf(taxChangeEffect, savingLambda)
        }
    }
}

fun main(args: Array<String>) {
//    val world = sugarWorld.World()
//    val country = sugarWorld.Country(1, 100, world)
//    val workforce = sugarWorld.Workforce(country)
//
//    for (i in 1..100) {
//        println(workforce.sellTime(100.0, 1.0))
//        println("Wages: ${workforce.mostRecentWages}, bank balance: ${workforce.bankBalance}")
//        println(workforce.giveTaxes(10.0))
//        println("Bank balance: ${workforce.bankBalance}")
//        workforce.step()
//        println("Bank balance: ${workforce.bankBalance}")
//    }

    for (i in -100..100) {
        println("${i.toDouble()}: proportion to save = ${Workforce.calculateProportionToSave(i.toDouble())}")
    }

//    println()
//    println()
//
//    for (i in -10..10) {
//        println(sugarWorld.Workforce.logistic(i.toDouble(), 0.0, 1.0))
//    }

//    println(workforce.sellTime(100.0, 1.5))
//    println(workforce.sellTime(100.0, 1.5))
//    println(workforce.sellTime(100.0, 2.5))
//    println(workforce.sellTime(100.0, 1.0))
//    println(workforce.sellTime(100.0, 0.5))
//    println(workforce.sellTime(100.0, 0.5))
//    println(workforce.sellTime(100.0, 0.25))
//    println(workforce.sellTime(100.0, 0.1))
//    println(workforce.sellTime(100.0, 0.0))
}
