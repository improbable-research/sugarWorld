import java.lang.Math.exp

class Workforce(val country: Country) : Consumer() {

    private val wageK = 0.5
    private val wageLambda = 2.0
    private val wageMidpointX = -0.5
    private val savingLambda = 1.0
    private var prevWagesInSugarPerDay = 1.0
    private var prevTaxes = 10.0
    private var taxChange = 0.0

    var mostRecentWages = 0.0


    fun sellTime(days: Double, wageInSugarPerDay: Double): Double {

        val absoluteWageEffect = expCdf(wageInSugarPerDay, wageLambda)

        val wageChange = wageInSugarPerDay - prevWagesInSugarPerDay
        val wageChangeEffect = logisticFunction(wageChange)

        val employmentRate = absoluteWageEffect * wageChangeEffect

        val daysOfWorkAvailable = country.population * employmentRate
        val daysOfWork = Math.min(daysOfWorkAvailable, days)

        mostRecentWages = wageInSugarPerDay * daysOfWork
        bankBalance += mostRecentWages
        prevWagesInSugarPerDay = wageInSugarPerDay

        return daysOfWork
    }

    fun giveTaxes(amnt: Double): Double {
        taxChange = amnt / prevTaxes
        prevTaxes = amnt
        val taxesGiven = Math.min(amnt, bankBalance)
        bankBalance -= taxesGiven
        return taxesGiven
    }

    fun step() {
        val proportionToSave = expCdf(taxChange, savingLambda)
        val expectedAfterTaxBankBalance = bankBalance - prevTaxes
        val expenditureOnSugar = expectedAfterTaxBankBalance * (1.0 - proportionToSave)
        country.industry.sellSugar(expenditureOnSugar)
        bankBalance -= expenditureOnSugar
    }

    private fun logisticFunction(x: Double): Double {
        return 1.0 / (1.0 + exp(-wageK * (x - wageMidpointX)))
    }

    private fun expCdf(x: Double, lambda: Double): Double {
        return 1.0 - exp(-x * lambda)
    }
}

fun main(args: Array<String>) {
    val world = World()
    val country = Country(1, 100, world)
    val workforce = Workforce(country)

    for (i in 1..100) {
        println(workforce.sellTime(100.0, 1.0))
        println("Wages: ${workforce.mostRecentWages}, bank balance: ${workforce.bankBalance}")
        println(workforce.giveTaxes(10.0))
        println("Bank balance: ${workforce.bankBalance}")
        workforce.step()
        println("Bank balance: ${workforce.bankBalance}")
    }

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
