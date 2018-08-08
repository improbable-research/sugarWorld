package sugarWorld

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class World(val printLogs: Boolean = false) {
    val gdp = arrayListOf<ArrayList<Double>>()

    val countries = arrayOf(
//            Country(1, 12000, this, printLogs),
//            Country(2, 20000, this, printLogs),
//            Country(3, 10000, this, printLogs),
//            Country(4, 15000, this, printLogs)
            Country(1, 10000, this, printLogs),
            Country(2, 10000, this, printLogs),
            Country(3, 10000, this, printLogs),
            Country(4, 10000, this, printLogs)
    )

    val propensityToTrade = arrayOf(
//            arrayOf(0.5, 0.5),
//            arrayOf(0.5, 0.5)
            arrayOf(0.5, 0.25, 0.25, 0.0),
            arrayOf(0.0, 0.5, 0.25, 0.25),
            arrayOf(0.25, 0.0, 0.5, 0.25),
            arrayOf(0.25, 0.25, 0.0, 0.5)
    )

    val transport = Transport()

    fun step(t: Int) {
        countries.forEach { it.step(t) }
        printStatus()
        recordData()
        countries.forEach { it.lateStep(t) }
        transport.step(t)
    }

    fun getPropensityToTradeWithSelf(country: Country): Double {
        val i = countries.indexOf(country)
        return propensityToTrade[i][i]
    }

    fun sellSugar(amnt: Double, buyersCountry: Country): Double {
        val buyerIdx = countries.indexOf(buyersCountry)
        val buyer = buyersCountry.industry
        val buyerTradePropensities = propensityToTrade[buyerIdx]
        val selfTradePropensity = buyerTradePropensities[buyerIdx]
        var totalBought = 0.0
        for ((i, p) in buyerTradePropensities.withIndex().filter { it.index != buyerIdx }) {
            val internationalTradePropensity = p / (1.0 - selfTradePropensity)
            val amntToBuy = amnt * internationalTradePropensity
            totalBought += countries[i].industry.exportSugar(amntToBuy, buyer)
        }

        return totalBought
    }

    fun writeGDPToCsv(filePath: String) {
        val dir = File(filePath).parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val writer = BufferedWriter(FileWriter(filePath))
        gdp.forEach {
            writer.write(it.joinToString())
            writer.newLine()
        }

        writer.close()
    }

    private fun printStatus() {
        countries.forEach { it.printStatus() }
        transport.printStatus()
    }

    private fun recordData() {
        val timestepGDP = arrayListOf<Double>()
        countries.forEach { timestepGDP.add(it.industry.salesThisStep) }
        gdp.add(timestepGDP)
    }
}
