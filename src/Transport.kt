class Transport {
    val deliveries = HashMap<Industry, Double>()

    fun pleaseDeliver(amnt: Double, destination :Industry) {
        deliveries.put(destination, amnt)
    }

    fun step() {
        deliveries.forEach({industry, amnt ->
            industry.acceptDelivery(amnt)
        })
        deliveries.clear()
    }
}