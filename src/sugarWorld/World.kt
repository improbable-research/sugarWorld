package sugarWorld

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class World(val printLogs: Boolean = false) {
    val gdp = arrayListOf<ArrayList<Double>>()
    val governmentBalance = arrayListOf<ArrayList<Double>>()
    val industryBalance = arrayListOf<ArrayList<Double>>()
    val workforceBalance = arrayListOf<ArrayList<Double>>()
    val industryStock = arrayListOf<ArrayList<Double>>()

    val countries = arrayOf(
            Country(1, 10000, this, printLogs),
            Country(2, 10000, this, printLogs),
            Country(3, 10000, this, printLogs),
            Country(4, 10000, this, printLogs)
    )

    val propensityToTrade = arrayOf(
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

    fun writeDataToCsv(data: ArrayList<ArrayList<Double>>, filePath: String) {
        val dir = File(filePath).parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val writer = BufferedWriter(FileWriter(filePath))
        writer.write(countries.map { "country_${it.id}" }.joinToString())
        writer.newLine()
        data.forEach {
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
        val timestepGovernmentBalance = arrayListOf<Double>()
        val timestepIndustryBalance = arrayListOf<Double>()
        val timestepWorkforceBalance = arrayListOf<Double>()
        val timestepIndustryStock = arrayListOf<Double>()
        countries.forEach { timestepGDP.add(it.industry.salesThisStep) }
        countries.forEach { timestepGovernmentBalance.add(it.government.bankBalance) }
        countries.forEach { timestepIndustryBalance.add(it.industry.bankBalance) }
        countries.forEach { timestepWorkforceBalance.add(it.workforce.bankBalance) }
        countries.forEach { timestepIndustryStock.add(it.industry.stock)}
        gdp.add(timestepGDP)
        governmentBalance.add(timestepGovernmentBalance)
        industryBalance.add(timestepIndustryBalance)
        workforceBalance.add(timestepWorkforceBalance)
        industryStock.add(timestepIndustryStock)
    }
}
