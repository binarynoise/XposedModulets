package com.programminghoch10.MotionEventMod

import android.app.Activity
import android.os.Bundle

class InputTestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        TODO("implement MotionEvent testing area with output of detected MotionEvents")
    }
    
    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }
}
