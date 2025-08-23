package com.programminghoch10.RotationControl

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.CheckBoxPreference

@SuppressLint("PrivateResource")
class RadioPreference(context: Context) : CheckBoxPreference(context) {
    
    init {
        widgetLayoutResource = R.layout.radio
    }
    
    override fun setChecked(checked: Boolean) {
        if (isChecked) return
        super.setChecked(checked)
    }
    
    fun onRadioPreferenceSelected(radioPreference: RadioPreference) {
        if (radioPreference == this) return
        super.setChecked(false)
    }
}
