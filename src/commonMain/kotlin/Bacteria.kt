import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import kotlin.random.Random

class Bacteria(
    private val views: Views,
    private val color: RGBA = getRandomColor()
) : Container() {
    private val view = solidRect(20, 20, color)
    private var sizeItem = 1
    var speedItem = 300.0
        private set
    private var movePosition = getRandomPosition()

    init {
        val thisContainer = this
        view.name = "Bacteria"
        val text = text("")
        text.addUpdater {
            this.text = "$sizeItem speed: $speedItem"
        }
        this.onCollision {
            if (it is Bacteria) {
                thisContainer.tryEat(it)
            }
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
        trayReproduction()
    }

    private fun trayReproduction() {
        if (sizeItem < 10) {
            return
        }
        val childrenColor = this.color
        val childrenX = this.x
        val childrenY = this.y

        parent!!.run {
            bacteria(views, childrenColor)
                .xy(childrenX, childrenY)
                .start()
            bacteria(views, childrenColor)
                .xy(childrenX, childrenY)
                .start()
            bacteria(views, childrenColor)
                .xy(childrenX, childrenY)
                .start()
        }
        kill()
    }

    private fun kill() {
        parent?.removeChild(this)
    }

    private fun tryEat(bacteria: Bacteria) {
        if (this.sizeItem > bacteria.sizeItem) {
            bacteria.kill()
            eatFood()
        }
    }

    fun start() {
        this.stage!!.runAnimation(this)
    }
}