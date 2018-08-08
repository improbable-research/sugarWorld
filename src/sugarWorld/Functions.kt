package sugarWorld


object Functions {

    fun logistic(x: Double, x0: Double, k: Double): Double {
        return 1.0 / (1.0 + Math.exp(-k * (x - x0)))
    }

    fun expCdf(x: Double, lambda: Double): Double {
        return 1.0 - Math.exp(-x * lambda)
    }

}
