package com.anwesh.uiprojects.linkedrotatinglineincircleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.rotatinglineincircleview.RotatingLineInCircleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : RotatingLineInCircleView = RotatingLineInCircleView.create(this)
        fullScreen()
        view.addOnAnimationCompleteListener({createToast("animation number $it is complete")},
                {createToast("animation numbner $it is reset")})
    }

    private fun createToast(txt : String) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}