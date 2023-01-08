import com.soywiz.korge.animate.AnimateCancellationException
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import kotlinx.coroutines.Job
import kotlin.random.Random

var idCounter = 0

class Bacteria(
    private val views: Views,
    val color: RGBA = getRandomColor(),
    val id: Int = idCounter++
) : Container() {
    private val view = solidRect(20, 20, color)
    private var sizeItem = 1
    var speedItem = 300.0
        private set
    private var movePosition = getRandomPosition()
    private var animationJob: Job? = null
    var isRetreat = false
        private set
    val collisionArea = solidRect(0, 0, Colors.BLACK)

    init {
        val thisContainer = this
        view.name = "Bacteria"
        val text = text("")
        text(id.toString())
            .xy(0, 15)
            .color = Colors.BLACK
        text.addUpdater {
            this.text = "$sizeItem speed: $speedItem"
        }
        view.onCollision {
            if (it.name == "Bacteria") {
                thisContainer.tryEat(it.parent as Bacteria)
            }
        }
        collisionArea.xy(-30, -30)
        collisionArea.addUpdater {
            this.scaledWidth = view.width + 60
            this.scaledHeight = view.height + 60
            this.alpha = 0.0
        }
        collisionArea.visible = false
        addChild(collisionArea)
        collisionArea.onCollision {
            if (it is Bacteria && it != thisContainer) {
                if (isRetreat) {
                    return@onCollision
                }
                if (it.canEat(thisContainer)) {
                    collisionArea.color = Colors.RED
                    it.collisionArea.color = Colors.GREEN
                    isRetreat = true
                    animationJob?.cancel(AnimateCancellationException(completeOnCancel = true))
                    val retreatPosition = getRetreatPosition(Position(it.x, it.y))
                    // thisContainer.parent!!.solidRect(10, 10, Colors.RED).position(retreatPosition)
                    movePosition = retreatPosition
                    start()
                }
            }
        }
    }

    private fun getRetreatPosition(dangerPoint: Position): Position {
        val newX = if (x < dangerPoint.x) x - 100 else x + 100
        val newY = if (y < dangerPoint.y) y - 100 else y + 100
        return Position(newX, newY)
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
            collisionArea.color = Colors.BLACK
            isRetreat = false
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

    private fun canEat(bacteria: Bacteria): Boolean {

        if (bacteria.color == this.color) {
            return false
        }

        return this.sizeItem - 1 > bacteria.sizeItem
    }

    private fun tryEat(bacteria: Bacteria) {
        if (this.canEat(bacteria)) {
            bacteria.kill()
            eatFood()
        }
    }

    fun start() {
        animationJob = this.stage!!.runAnimation(this)
    }
}