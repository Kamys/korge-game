import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import kotlin.random.Random

class Bacteria(private val views: Views) : Container() {
    private val view = solidRect(20, 20, Colors["#1976d2"])
    var sizeItem = 0
        private set
    var speedItem = 300.0
        private set
    private var movePosition = getRandomPosition()


    init {
        view.name = "Bacteria"
        val text = text("0 text, speed: $speedItem")
        text.addUpdater {
            this.text = "$sizeItem speed: $speedItem"
        }
    }

    private fun getRandomPosition(): Position {
        return Position(
            x = Random.nextDouble(views.virtualWidth.toDouble()),
            y = Random.nextDouble(views.virtualHeight.toDouble())
        )
    }

    private fun updateMovePosition() {
        val isXOnPosition = movePosition.x.almostEqual(x, 10.0)
        val isYOnPosition = movePosition.y.almostEqual(y, 10.0)
        if (isXOnPosition && isYOnPosition) {
            movePosition = getRandomPosition()
        }
    }

    fun go(): Position {
        updateMovePosition()
        return movePosition
    }

    fun eatFood() {
        view.scaledHeight = view.height + 10
        view.scaledWidth = view.width + 10
        sizeItem++
        if (speedItem > 100) {
            speedItem -= 30
        }
    }

    fun tryEat(bacteria: Bacteria) {
        if (this.sizeItem - 2 > bacteria.sizeItem) {
            bacteria.parent?.removeChild(bacteria)
        }
    }
}