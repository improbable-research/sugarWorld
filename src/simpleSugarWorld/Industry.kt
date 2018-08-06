package simpleSugarWorld

import simpleSugarWorld.Functions.logistic
import kotlin.math.max
import kotlin.math.min

class Industry(val country: Country) : Consumer(bankBalance = country.population.toDouble()) {
    val targetStock = 10000.0
    var stock = 10000.0
    var salesTwoStepsAgo = 0.0
    var salesLastStep = 0.0
    var salesThisStep = 0.0
    var labourDeficit = 0.0
    val valueAdded = 1.5 // output per unit input
    val baselineLabour = 10.0
    val labourPerUnitProduction = 0.8 // days work per unit production
    val labourExpenditureK = 0.5
    val labourExpenditureMidpoint_x0 = 2.0

    fun sellSugar(amnt: Double): Double {
        val amntSold = max(0.0, min(amnt, stock))
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
    }

    private fun manufactureProduct() {
        val expectedSales = expectedSalesNextStep()
        val extraStockNeeded = max(0.0, expectedSales + (targetStock - stock))
        val stockToProduce = extraStockNeeded * country.world.getPropensityToTradeWithSelf(country)
        val stockToImport = extraStockNeeded - stockToProduce
        bankBalance -= country.world.sellSugar(stockToImport, country)

        val requiredLabourDays = baselineLabour + (stockToProduce * labourPerUnitProduction)
        val priceOfLabourPerDay = priceOfLabour(expectedSales, requiredLabourDays)
        val acquiredLabourDays = country.workforce.sellTime(requiredLabourDays, priceOfLabourPerDay)
        labourDeficit = (requiredLabourDays - acquiredLabourDays) / requiredLabourDays

        val production = acquiredLabourDays / labourPerUnitProduction
        stock += production
        log("Expected sales $expectedSales, extra needed $extraStockNeeded, importing $stockToImport, " +
                "producing $stockToProduce, labour required $requiredLabourDays, wage $priceOfLabourPerDay, " +
                "acquired labour $acquiredLabourDays, production $production, stock $stock, bank balance $bankBalance")
    }

    fun expectedSalesNextStep(): Double {
        log("Sales t-2 $salesTwoStepsAgo, sales t-2 $salesLastStep, expected sales ${salesLastStep + (salesLastStep - salesTwoStepsAgo)}")
        return Math.max(0.0, salesLastStep + (salesLastStep - salesTwoStepsAgo))
//        return salesLastStep
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
