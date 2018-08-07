package newSugarWorld


class Country(val id: Int, val world: World, val population: Double) {

    val government = Government(country = this, bankBalance = population)
    val workforce = Workforce(country = this, bankBalance = population)
    val industry = Industry(country = this, bankBalance = population, stock = population)

    fun step(t: Int) {
        government.step(t)
        workforce.step(t)
        industry.step(t)
    }

    fun lateStep(t: Int) {
        industry.lateStep(t)
    }

    fun report(t: Int) {
        println("[Country $id] (t:$t) GDP ${industry.getLatestSales()}, IND ${industry.bankBalance}, GOV ${government.bankBalance}, WRK ${workforce.bankBalance}, stock ${industry.stock}")
    }
}
