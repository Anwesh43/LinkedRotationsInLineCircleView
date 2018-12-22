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