package simpleSugarWorld

class Transport {
    val deliveries = HashMap<Industry, Double>()

    fun pleaseDeliver(amnt: Double, destination : Industry) {
        deliveries.put(destination, amnt)
    }

    fun step(t: Int) {
        deliveries.forEach({industry, amnt ->
            industry.acceptDelivery(amnt)
        })
        deliveries.clear()
    }

    fun printStatus() {
        val totalInTransit = deliveries.values.sum()
        println("[Transport] Goods in transit: $totalInTransit")
    }
}
