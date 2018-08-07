package newSugarWorld


class World {

    val countries = listOf(
            Country(1, this, 10000.0)
    )

    val importPropensities = listOf(
            listOf(1.0)
    )

    val inTransit = mutableMapOf<Country, Double>()

    fun step(t: Int) {
        completeDeliveries()
        countries.forEach { it.step(t) }
        report(t)
    }

    fun getId(country: Country): Int {
        return countries.indexOf(country)
    }

    fun getImportRatio(country: Country): Double {
        val id = getId(country)
        return 1.0 - importPropensities[id][id]
    }

    fun import(requester: Country, amount: Double): Double {
        val id = getId(requester)
        val requesterImportPropensities = importPropensities[id]

        var amountToBeDelivered = 0.0
        for (i in (0 until countries.size).filter { it != id }) {
            val importPartner = countries[i]
            val importPropensity = requesterImportPropensities[i]
            val amountFromCountry = importPropensity * amount
            val amountOrdered = importPartner.industry.sellProduct(amountFromCountry)
            amountToBeDelivered += amountOrdered
        }

        deliverTo(requester, amountToBeDelivered)

        return amountToBeDelivered
    }

    private fun deliverTo(country: Country, amount: Double) {
        inTransit.put(country, inTransit.getOrDefault(country, 0.0) + amount)
    }

    private fun completeDeliveries() {
        for ((country, amount) in inTransit) {
            country.industry.acceptDelivery(amount)
        }
    }

    private fun report(t: Int) {
        countries.forEach { it.report(t) }
    }
}
