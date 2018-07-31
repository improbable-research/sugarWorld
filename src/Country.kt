class Country(val id: Int, val population : Int, val world : World) {
    val government = Government(this)
    val workforce = Workforce(this)
    val industry = Industry(this)

    fun step() {
        workforce.step()
        government.step()
        industry.step()
    }

    fun printStatus() {
        val govBank = government.bankBalance
        val workBank = workforce.bankBalance
        val indBank = industry.bankBalance
        val totalBank = govBank + workBank + indBank
        val stock = industry.stock
        println("[Country $id] Total: $totalBank Gov: $govBank, Work: $workBank, Ind: $indBank, Stock: $stock")
    }
}
