class Industry(country : Country) : Consumer() {
    var stock = 1000.0

    fun sellSugar(amnt : Double) : Double {
        return 0.0
    }
    fun exportSugar(amnt : Double, buyer : Industry) : Double {
        return 0.0
    }
    fun acceptDelivery(amnt : Double) {

    }

    fun step() {}
}