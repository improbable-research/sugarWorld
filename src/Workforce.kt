import java.lang.Math.exp

class Workforce(val country: Country) : Consumer() {

    private val k = 1.0
    private val lamda = 1.0
    private var prevWagesInSugarPerDay = 1.0
    private var prevTaxes = 0.0
    private var taxChange = 0.0


    fun sellTime(days: Double, wageInSugarPerDay: Double): Double {
        val x = wageInSugarPerDay - prevWagesInSugarPerDay
        val employmentRate = logisticFun(x)
        val daysOfWorkAvailable = country.population * employmentRate
        val daysOfWork = Math.min(daysOfWorkAvailable, days)
        val wagesReceived = wageInSugarPerDay * daysOfWork
        bankBalance += wagesReceived
        prevWagesInSugarPerDay = wageInSugarPerDay
        return daysOfWork
    }

    fun giveTaxes(amnt: Double): Double {
        taxChange = amnt / prevTaxes
        prevTaxes = amnt
        return Math.min(amnt, bankBalance)
    }

    fun step() {
        val proportionToSave = expCdf(taxChange)
        val expectedAfterTaxBankBalance = bankBalance - prevTaxes
        val expenditureOnSugar = expectedAfterTaxBankBalance * (1.0 - proportionToSave)
        country.industry.sellSugar(expenditureOnSugar)
    }

    private fun logisticFun(x: Double): Double {
        return 1.0 / (1.0 + exp(-k * x))
    }

    private fun expCdf(x: Double): Double {
        return 1.0 - exp(-x / lamda)
    }
}

fun main(args: Array<String>) {
    
}
