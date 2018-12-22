package com.anwesh.uiprojects.linkedrotatinglineincircleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.rotatinglineincircleview.RotatingLineInCircleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RotatingLineInCircleView.create(this)
    }
}
