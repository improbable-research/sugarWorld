package newSugarWorld


class Government(val country: Country, var bankBalance: Double) {

    var taxRate = 0.25
    var industryStimulationRate = 0.5

    fun step(t: Int) {
//        bankBalance += country.workforce.payTaxes(taxRate)
//
//        val productToBuy = bankBalance * industryStimulationRate
//        val productBought = country.industry.sellProduct(productToBuy)
//
//        log("Wanted to buy $productToBuy, bought $productBought")
//
//        bankBalance -= productBought
    }

    private fun log(msg: String) {
        println("[GOV ${country.id}] $msg")
    }
}
