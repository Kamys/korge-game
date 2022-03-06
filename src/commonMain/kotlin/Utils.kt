import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA

data class Position(val x: Double, val y: Double)

fun View.position(position: Position) {
    this.position(x = position.x, y = position.y)
}

fun Double.almostEqual(value: Double, eps: Double): Boolean {
    return this in (value - eps)..(value + eps);
}

fun getRandomColor(): RGBA {
    return Colors.colorsByName.values.random()
}

fun Container.bacteria(views: Views, color: RGBA = getRandomColor()): Bacteria {
    val bacteria = Bacteria(views, color)
    bacteria.addTo(this)
    this.addChild(bacteria)
    return bacteria
}