package com.anwesh.uiprojects.rotatinglineincircleview

/**
 * Created by anweshmishra on 22/12/18.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.content.Context

val nodes : Int = 5
val rotations : Int = 2
val scGap : Float = 0.05f
val scDiv : Double = 0.51
val color : Int = Color.parseColor("#4527A0")
val sizeFactor : Float = 2.7f
val strokeFactor : Int = 90
val MAX_DEG : Float = 90f

fun Int.inverse() : Float = 1f / this

fun Float.maxOfScaleParts(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxOfScaleParts(i, n)) * n

fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()

fun Float.mirrorValue(a : Int, b : Int) : Float = (1 - scaleFactor()) * a.inverse() + scaleFactor() * b.inverse()

fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * dir * scGap

fun Int.percentileMirror() : Int = 1 - 2 * (this % 2)

fun Canvas.drawRLICNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    val r : Float = size/3
    paint.color = color
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    paint.style = Paint.Style.STROKE
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    save()
    translate(gap * (i +1), h/2)
    drawCircle(0f, 0f, r, paint)
    var currDeg : Float = 0f
    for(j in 0..(rotations - 1)) {
        val sc : Float = sc2.divideScale(j, rotations)
        currDeg += MAX_DEG * sc * (1 - 2 * j.percentileMirror())
    }
    rotate(currDeg)
    drawLine(0f, 0f, 0f, -size * sc1, paint)
    restore()
}

class RotatingLineInCircleView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateScale(dir, 1, rotations)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class RLICNode(var i : Int, val state : State = State()) {

        private var next : RLICNode? = null

        private var prev : RLICNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = RLICNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRLICNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RLICNode {
            var curr : RLICNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class RotatingLineInCircle(var i : Int) {

        private val root : RLICNode = RLICNode(0)
        private var curr : RLICNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : RotatingLineInCircleView) {

        private val animator : Animator = Animator(view)
        private val rlic : RotatingLineInCircle = RotatingLineInCircle(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            rlic.draw(canvas, paint)
            animator.animate {
                rlic.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            rlic.startUpdating {
                animator.start()
            }
        }
    }
}