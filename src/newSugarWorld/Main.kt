package newSugarWorld


fun main(args: Array<String>) {

    val world = World()

    for (t in 1..10) {
        world.step(t)
    }
}
