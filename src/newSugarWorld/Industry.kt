package newSugarWorld

import kotlin.math.max
import kotlin.math.min


class Industry(val country: Country, var stock: Double, var bankBalance: Double) {

    val salesRecord = arrayListOf(country.workforce.minConsumption)
    val stockRecord = arrayListOf(stock)

    val baselineLabourUnitsRequired = 10.0
    val targetStock = 10000.0
    val productionPerUnitWork = 2.0

    var wagePerUnitWork = 0.8

    var stockDeficit = 0.0

    var sales = 0.0
    var salesProjection = country.workforce.minConsumption


    fun step(t: Int) {
        val extraStockNeeded = max(0.0, stockDeficit + salesProjection)
        val importsOrdered = orderImports(extraStockNeeded)
        val domesticProduction = manufacture(extraStockNeeded, importsOrdered)
        stock += domesticProduction
        log("Stock $stock, prod $domesticProduction, stockDeficit ${stockDeficit}")
    }

    fun lateStep(t: Int) {
        salesRecord.add(sales)
        sales = 0.0
        salesProjection = projectSales()

        stockRecord.add(stock)
        stockDeficit = targetStock - stock
    }

    fun sellProduct(amount: Double): Double {
        val sold = min(amount, stock)
        bankBalance += sold
        sales += sold
        stock -= sold
        return sold
    }

    fun acceptDelivery(amount: Double) {
        stock += amount
    }

    fun getLatestSales(): Double {
        return salesRecord[salesRecord.size - 1]
    }

    private fun projectSales(): Double {
        val prevT2 = salesRecord.getOrElse(salesRecord.size - 2, { i -> country.workforce.minConsumption })
        val prevT1 = salesRecord[salesRecord.size - 1]
        return prevT1 + (prevT1 - prevT2)
    }

    private fun orderImports(extraStockNeeded: Double): Double {
        val importRatio = country.world.getImportRatio(country)
        val amountToImport = extraStockNeeded * importRatio
        val amountBeingDelivered = country.world.import(country, amountToImport)
        return amountBeingDelivered
    }

    private fun manufacture(extraStockNeeded: Double, importsOrdered: Double): Double {
        val amountToManufacture = extraStockNeeded - importsOrdered
        val labourRequired = baselineLabourUnitsRequired + (amountToManufacture / productionPerUnitWork)
        wagePerUnitWork = (bankBalance * 0.5) / labourRequired
        val labourHired = country.workforce.employLabour(labourRequired, wagePerUnitWork)
        bankBalance -= labourHired * wagePerUnitWork
        val amountProduced = labourHired * productionPerUnitWork
        log("Labour req $labourRequired, hired $labourHired")
        return amountProduced
    }

    private fun log(msg: String) {
        println("[IND ${country.id}] $msg")
    }
}
