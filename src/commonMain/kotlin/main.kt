import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.animate.animateSequence
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.interpolation.Easing
import kotlin.random.Random

suspend fun main() = Korge(bgcolor = Colors["#78909c"]) {
    val main = this
    fun getRandomPosition(): Position {
        return Position(
            x = Random.nextDouble(views.virtualWidth.toDouble()),
            y = Random.nextDouble(views.virtualHeight.toDouble())
        )
    }

    fun View.positionRandom() {
        position(getRandomPosition())
    }

    fun addFood() {
        val a = solidRect(10, 10, Colors["#00c853"])
        a.positionRandom()
        a.onCollision {
            if (it is Bacteria) {
                main.removeChild(a)
                it.eatFood()
            }
        }
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
        addFood()
    }

    var items = listOf(
        Bacteria(views),
        Bacteria(views),
        Bacteria(views),
        Bacteria(views),
        Bacteria(views),
    )

    items.forEach {
        it.positionRandom()
        addChild(it)
        it.onCollision { item2 ->
            if (item2 is Bacteria) {
                it.tryEat(item2)
            }
        }
    }

    launchImmediately {
        while (true) {
            delay(1.seconds)
            repeat(5) {
                addFood()
            }
        }
    }

    launchImmediately {
        while (true) {
            delay(2.seconds)
            items = items + Bacteria(views)
        }
    }

    items.forEach {
        launchImmediately {
            while (true) {
                animateSequence {
                    parallel {
                        val position = it.go()
                        it.moveToWithSpeed(position.x, position.y, it.speedItem, Easing.LINEAR)
                    }
                    block { }
                }
            }
        }
    }
}