import java.lang.Math.exp

class Workforce(val country: Country) : Consumer() {

    private var prevWagesInSugarPerDay = 1.0
    private var prevTaxes = 10.0
    private var taxChange = 1.0

    var mostRecentWages = country.population.toDouble()


    fun sellTime(days: Double, wageInSugarPerDay: Double): Double {
        log("Asked for $days days at $wageInSugarPerDay")

        val correctedWageInSugarPerDay = Math.max(wageInSugarPerDay, 0.0)
        val wageChangeInSugarPerDay = correctedWageInSugarPerDay - prevWagesInSugarPerDay
        val employmentRate = calculateEmploymentRate(wageInSugarPerDay, wageChangeInSugarPerDay)

        val daysOfWorkAvailable = country.population * employmentRate
        val daysOfWork = Math.min(daysOfWorkAvailable, days)

        log("Providing $daysOfWork days of work")

        mostRecentWages = correctedWageInSugarPerDay * daysOfWork
        bankBalance += mostRecentWages
        prevWagesInSugarPerDay = correctedWageInSugarPerDay

        log("Wages $mostRecentWages, bank balance $bankBalance")

        return daysOfWork
    }

    fun giveTaxes(amnt: Double): Double {
        taxChange = amnt - prevTaxes
        log("Tax change $taxChange, taxes $amnt, prev taxes $prevTaxes")
        prevTaxes = amnt
        val taxesGiven = Math.min(amnt, bankBalance)
        bankBalance -= taxesGiven
        log("Gave $taxesGiven tax, bank balance $bankBalance")
        return taxesGiven
    }

    fun step() {
        val proportionToSave = calculateProportionToSave(taxChange)
        val expectedAfterTaxBankBalance = bankBalance - prevTaxes
        val expenditureOnSugar = expectedAfterTaxBankBalance * (1.0 - proportionToSave)
        country.industry.sellSugar(expenditureOnSugar)
        bankBalance -= expenditureOnSugar
        log("Saving proportion $proportionToSave, spending $expenditureOnSugar, bank balance $bankBalance")
    }

    private fun log(msg: String) {
        println("[WF${country.id}] msg")
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
//    val world = World()
//    val country = Country(1, 100, world)
//    val workforce = Workforce(country)
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
//        println(Workforce.logisticFunction(i.toDouble(), 0.0, 1.0))
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
