import kotlin.math.abs
import kotlin.math.min

fun differentiableMin(x : Double, y : Double, curvatureHalfWidth : Double) : Double {
    if(abs(x-y) > curvatureHalfWidth) {
        return min(x,y)
    } else  {
        return (x+y)/2.0 - (x-y)*(x-y)/(4.0*curvatureHalfWidth) - curvatureHalfWidth/4.0
    }
}

fun main(args : Array<String>) {
    var x = -1.0
    var y = 0.0
    while(x < 1.0) {
        x += 0.05
        y = -1.0
        while(y < 1.0) {
            y += 0.05
            println("$x $y ${differentiableMin(x,y,0.2)}")
        }
        println("")
    }


}