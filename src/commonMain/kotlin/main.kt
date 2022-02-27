import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.animate.animate
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.interpolation.Easing
import kotlin.random.Random

data class Position(val x: Double, val y: Double)

fun View.position(position: Position) {
    this.position(x = position.x, y = position.y)
}

class Item : Container() {
    private val view = solidRect(20, 20, Colors["#1976d2"])
    var sizeItem = 0
        private set
    var speedItem = 300.0
        private set



    init {
        view.name = "Item"
        val text = text("0 text, speed: $speedItem")
        text.addUpdater {
            this.text = "$sizeItem speed: $speedItem"
        }
        addChild(view)
        addChild(text)
    }

    fun calcPosition(views: Views): Position {
        return Position(
            x = Random.nextDouble(views.virtualWidth.toDouble()),
            y = Random.nextDouble(views.virtualHeight.toDouble())
        )
    }

    fun eatFood() {
        view.scaledHeight = view.height + 10
        view.scaledWidth = view.width + 10
        sizeItem++
        if (speedItem > 100) {
            speedItem -= 30
        }
    }

    fun tryEat(item: Item) {
        if (this.sizeItem > item.sizeItem) {
            item.parent?.removeChild(item)
        }
    }
}

suspend fun main() = Korge(bgcolor = Colors["#78909c"]) {
    val main = this
    fun View.positionRandom() {
        position(
            x = Random.nextDouble(views.virtualWidth.toDouble()),
            y = Random.nextDouble(views.virtualHeight.toDouble())
        )
    }

    val solid = solidRect(10, 10, Colors.WHITE).xy(0, 0)
    solid.name = "Mouse solid"

    launchImmediately {
        while (true) {
            solid.x = mouseX - (solid.width / 2)
            solid.y = mouseY - (solid.height / 2)
            delay(16.milliseconds) // suspending
        }
    }

    repeat(30) {
        val name = it.toString()
        val a = solidRect(10, 10, Colors["#00c853"])
        a.setText(name)
        a.positionRandom()
        a.onCollision {
            if (it is Item) {
                main.removeChild(a)
                it.eatFood()
            }
        }

    }

    val items = listOf(
        Item(),
        Item(),
        Item(),
        Item(),
        Item(),
    )

    items.forEach {
        it.positionRandom()
        addChild(it)
        it.onCollision { item2 ->
            if (item2 is Item) {
                it.tryEat(item2)
            }
        }
    }

    showAnimation(items)
}

fun Stage.showAnimation(items: List<Item>) = launchImmediately {
    val stage = this
    while (true) {
        animate {
            parallel {
                items.forEach {
                    val position = it.calcPosition(stage.views)
                    it.moveToWithSpeed(position.x, position.y, it.speedItem, Easing.LINEAR)
                }
            }
            block { }
        }
        delay(16.milliseconds)
    }
}