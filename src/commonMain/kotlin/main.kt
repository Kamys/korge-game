import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korge.Korge
import com.soywiz.korge.animate.animateSequence
import com.soywiz.korge.service.storage.Storage
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.async
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.interpolation.Easing
import kotlin.random.Random

suspend fun main() = Korge(bgcolor = Colors["#78909c"]) {
    val main = this

    fun Stage.getRandomPosition(): Position {
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

    val items = listOf(
        Bacteria(views),
        Bacteria(views),
        Bacteria(views),
        Bacteria(views),
        Bacteria(views),
    )

    items.forEach {
        it.positionRandom()
        addChild(it)
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
            val view = Bacteria(views)
            view.positionRandom()
            addChild(view)
            runAnimation(view)
        }
    }

    items.forEach {
        runAnimation(it)
    }
}

fun Stage.runAnimation(view: Bacteria) {
    launchImmediately {
        while (view.parent != null) {
            animateSequence {
                parallel {
                    val position = view.go()
                    view.moveToWithSpeed(position.x, position.y, view.speedItem, Easing.LINEAR)
                }
                block { }
            }
        }
    }
}