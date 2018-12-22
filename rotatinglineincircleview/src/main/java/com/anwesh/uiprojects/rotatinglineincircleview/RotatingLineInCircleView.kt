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