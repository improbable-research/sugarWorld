fun main(args : Array<String>) {
    val world = World()

    for(t in 1..100) {
        world.step()
    }
}
