package com.medisana.dagger2demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Dagger2Activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger2)
    }

    fun jumpSecondActivity(view: View) {}
}
