class Country(val population : Int, val world : World) {
    val government = Government(this)
    val workforce = Workforce(this)
    val industry = Industry(this)

    fun step() {
        workforce.step()
        government.step()
        industry.step()
    }
}