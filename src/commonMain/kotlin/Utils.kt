import com.soywiz.korge.view.View
import com.soywiz.korge.view.position

data class Position(val x: Double, val y: Double)

fun View.position(position: Position) {
    this.position(x = position.x, y = position.y)
}

fun Double.almostEqual(value: Double, eps: Double): Boolean {
    return this in (value-eps)..(value+eps);
}