package sugarWorld

import kotlin.math.min

class Industry(val country: Country) : Consumer(bankBalance = 1000.0) {
    val targetStock = 1000.0
    var stock = 10000.0
    var salesLastStep = 0.0
    var salesThisStep = 0.0
    val valueAdded = 1.5 // output per unit input
    val baselineLabour = 10.0
    val labourPerUnitProduction = 0.8 // days work per unit production

    fun sellSugar(amnt: Double): Double {
        val amntSold = min(amnt, stock)
        stock -= amntSold
        bankBalance += amntSold
        salesThisStep += amntSold
        log("Sold $amnt (this step $salesThisStep), bank balance $bankBalance, stock $stock")
        return amntSold
    }

    fun exportSugar(amnt: Double, buyer: Industry): Double {
        val amntSold = sellSugar(amnt)
        if (buyer != this) {
            country.world.transport.pleaseDeliver(amntSold, buyer)
        } else {
            bankBalance += amntSold
            acceptDelivery(amntSold)
        }
        return amntSold
    }

    fun acceptDelivery(amnt: Double) {
        stock += amnt
    }

    fun step(t: Int) {
        log("Step $t:")
        manufactureProduct()
    }

//    fun manufactureProduct() {
//        val expectedSales = expectedSalesNextStep()
//        log("Sales this step $salesThisStep, last step $salesLastStep, expected sales total $expectedSales")
//        val additionalStockRequired = max(0.0, expectedSales + targetStock - stock)
//        val rawMaterialsRequired = additionalStockRequired / valueAdded
//        bankBalance -= country.world.sellSugar(rawMaterialsRequired, country)
////        val expectedSales = rawMaterialsRequired * valueAdded
//        val requiredLabour = (additionalStockRequired * labourPerUnitProduction) + baselineLabour
//        val priceOfLabour = priceOfLabour(expectedSales, requiredLabour)
//        val acquiredLabour = country.workforce.sellTime(requiredLabour, priceOfLabour)
//        val production = min(acquiredLabour / labourPerUnitProduction, stock * valueAdded)
//        stock += production
//        log("Expected sales net $expectedSales, raw materials required $rawMaterialsRequired, labour required $requiredLabour, " +
//                "wage $priceOfLabour, acquired labour $acquiredLabour, production $production, stock $stock, bank balance $bankBalance")
//        salesLastStep = salesThisStep
//        salesThisStep = 0.0
//    }

    fun manufactureProduct() {
        val expectedSalesTotal = expectedSalesNextStep()
        log("Sales this step $salesThisStep, last step $salesLastStep, expected sales total $expectedSalesTotal")
        val requiredRawMaterials = 2.0 * (expectedSalesTotal / valueAdded) * (1.0 - country.world.getPropensityToTradeWithSelf(country) / valueAdded)
        bankBalance -= country.world.sellSugar(requiredRawMaterials, country)
        val expectedSales = requiredRawMaterials * valueAdded
        val requiredLabour = (expectedSales * labourPerUnitProduction) + baselineLabour
        val priceOfLabour = priceOfLabour(expectedSales, requiredLabour)
        val acquiredLabour = country.workforce.sellTime(requiredLabour, priceOfLabour)
        val production = min(acquiredLabour / labourPerUnitProduction, stock * valueAdded)
        stock += production
        log("Expected sales net $expectedSales, raw materials required $requiredRawMaterials, labour required $requiredLabour, " +
                "wage $priceOfLabour, acquired labour $acquiredLabour, production $production, stock $stock, bank balance $bankBalance")
        salesLastStep = salesThisStep
        salesThisStep = 0.0
    }

    fun expectedSalesNextStep(): Double {
//        return Math.max(0.0, (2.0 * salesThisStep + salesLastStep) / 3.0)
        return Math.max(0.0, salesThisStep + (salesThisStep - salesLastStep))
    }

    fun priceOfLabour(expectedSales: Double, requiredLabour: Double): Double {
        val expectedProfit = expectedSales * (valueAdded - 1.0)
//        return (expectedProfit + 0.1 * (bankBalance - 1500.0)) / requiredLabour
        return expectedProfit / requiredLabour
    }

    private fun log(msg: String) {
        if (country.printLogs) {
            println("[IND${country.id}] $msg")
        }
    }
}
