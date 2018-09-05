package sugarWorld

class Country(val id: Int, val population : Int, val world : World, val printLogs: Boolean) {
    val government = Government(this)
    val workforce = Workforce(this)
    val industry = Industry(this)

    fun step(t: Int) {
        workforce.step(t)
        government.step(t)
        industry.step(t)
    }

    fun lateStep(t: Int) {
        industry.lateStep(t)
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
