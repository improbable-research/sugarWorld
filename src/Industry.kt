import kotlin.math.min

class Industry(val country: Country) : Consumer() {
    var stock = 1000.0
    var salesLastStep = 0.0
    var salesThisStep = 0.0
    val valueAdded = 1.2 // output per unit input
    val labourPerUnitProduction = 0.8 // days work per unit production

    fun sellSugar(amnt: Double): Double {
        val amntSold = min(amnt, stock)
        stock -= amntSold
        bankBalance += amntSold
        salesThisStep += amntSold
        return amntSold
    }

    fun exportSugar(amnt: Double, buyer: Industry): Double {
        val amntSold = sellSugar(amnt)
        if (buyer != this) {
            country.world.transport.pleaseDeliver(amntSold, buyer)
        } else {
            bankBalance -= amntSold
            acceptDelivery(amntSold)
        }
        return amntSold
    }

    fun acceptDelivery(amnt: Double) {
        stock += amnt
    }

    fun step() {
//        val requiredRawMaterials = (expectedSalesNextStep() / valueAdded) * (1.0 - country.world.getPropensityToTradeWithSelf(country) / valueAdded)
        val requiredRawMaterials = (expectedSalesNextStep() * (1.0 - country.world.getPropensityToTradeWithSelf(country))) / valueAdded
        println("Required raw materials $requiredRawMaterials")
        bankBalance -= country.world.sellSugar(requiredRawMaterials, country)
        val expectedSales = requiredRawMaterials * valueAdded
        println("Expected sales $expectedSales")
        val requiredLabour = expectedSales * labourPerUnitProduction
        println("Required labour $requiredLabour")
        val priceOfLabour = priceOfLabour(expectedSales)
        val acquiredLabour = country.workforce.sellTime(requiredLabour, priceOfLabour)
        println("Acquired labour $acquiredLabour")
        val production = min(acquiredLabour / labourPerUnitProduction, stock * valueAdded)
        println("Production $production")
        stock += production
        salesLastStep = salesThisStep
        salesThisStep = 0.0
    }

    fun expectedSalesNextStep(): Double {
        return Math.max(0.0, 2.0 * salesThisStep - salesLastStep)
    }

    fun priceOfLabour(expectedSales: Double): Double {
        val requiredLabour = expectedSales * labourPerUnitProduction
//        val expectedProfit = expectedSales * (1.0 - valueAdded)
        val expectedProfit = expectedSales * (valueAdded - 1.0)
        println("Expected profit $expectedProfit")
        return (expectedProfit + 0.1 * (bankBalance - 1500.0)) / requiredLabour
    }
}
