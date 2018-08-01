fun main(args : Array<String>) {
    val world = World()

    for(t in 1..5) {
        world.step()
        print("$t ")
        for(country in world.countries) {
            print("${country.industry.salesThisStep} ")
        }
        println("")
    }
}
