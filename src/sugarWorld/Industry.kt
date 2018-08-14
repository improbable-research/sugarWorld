package sugarWorld

import sugarWorld.Functions.expCdf
import sugarWorld.Functions.logistic
import kotlin.math.max
import kotlin.math.min

class Industry(val country: Country) : Consumer(bankBalance = country.population.toDouble()) {
    var stock = 10000.0
    var stockDeficit = 0.0

    var targetStock = 10000.0
    var salesTwoStepsAgo = country.workforce.baselineExpenditure
    var salesLastStep = country.workforce.baselineExpenditure
    var salesThisStep = 0.0
    var labourDeficit = 0.0

    companion object {
        const val baselineLabour = 10.0
        const val labourPerUnitProduction = 0.8 // days work per unit production
        const val labourExpenditureK = 0.5
        const val labourExpenditureMidpoint_x0 = 2.0
        const val sellingK = 1.0
    }

    fun sellSugar(amnt: Double): Double {
        val amntSold = amnt * logistic(stock, amnt, sellingK)
        stock -= amntSold
        bankBalance += amntSold
        salesThisStep += amntSold
        log("Sold $amnt (this step $salesThisStep), bank balance $bankBalance, stock $stock")
        return amntSold
    }

    fun exportSugar(amnt: Double, buyer: Industry): Double {
        val amntSold = sellSugar(amnt)
        country.world.transport.pleaseDeliver(amntSold, buyer)
        log("Exported $amntSold to ${buyer.country.id}")
        return amntSold
    }

    fun acceptDelivery(amnt: Double) {
        stock += amnt
    }

    fun step(t: Int) {
        log("Step $t:")
        manufactureProduct()
    }

    fun lateStep(t: Int) {
        salesTwoStepsAgo = salesLastStep
        salesLastStep = salesThisStep
        salesThisStep = 0.0

        stockDeficit = targetStock - stock
    }

    private fun manufactureProduct() {
        val expectedSales = expectedSalesNextStep()
        val extraStockNeeded = max(0.0, expectedSales + stockDeficit)
        val stockToProduce = extraStockNeeded * country.world.getPropensityToTradeWithSelf(country)
        val stockToImport = min(extraStockNeeded - stockToProduce, bankBalance)
        bankBalance -= country.world.sellSugar(stockToImport, country)

        val requiredLabourDays = baselineLabour + (stockToProduce * labourPerUnitProduction)
        val priceOfLabourPerDay = priceOfLabour(expectedSales, requiredLabourDays)
        val acquiredLabourDays = country.workforce.sellTime(requiredLabourDays, priceOfLabourPerDay)
        labourDeficit = requiredLabourDays - acquiredLabourDays

        val production = acquiredLabourDays / labourPerUnitProduction
        stock += production
        log("Expected sales $expectedSales, extra needed $extraStockNeeded, importing $stockToImport, " +
                "producing $stockToProduce, labour required $requiredLabourDays, wage $priceOfLabourPerDay, " +
                "acquired labour $acquiredLabourDays, production $production, stock $stock, bank balance $bankBalance")
    }

    fun expectedSalesNextStep(): Double {
        log("Sales t-2 $salesTwoStepsAgo, sales t-1 $salesLastStep, expected sales ${salesLastStep + (salesLastStep - salesTwoStepsAgo)}")
        return Math.max(0.0, salesLastStep + (salesLastStep - salesTwoStepsAgo))
    }

    fun priceOfLabour(expectedSales: Double, requiredDaysLabour: Double): Double {
        val fundsAvailableForLabour = bankBalance + expectedSales
        val proportionToSpendOnLabour = logistic(labourDeficit, labourExpenditureMidpoint_x0, labourExpenditureK)
        log("Spending proportion on labour $proportionToSpendOnLabour")
        return (fundsAvailableForLabour * proportionToSpendOnLabour) / requiredDaysLabour
    }

    private fun log(msg: String) {
        if (country.printLogs) {
            println("[IND${country.id}] $msg")
        }
    }
}
