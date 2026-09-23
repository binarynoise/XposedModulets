package com.programminghoch10.MotionEventMod

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.preference.Preference

class TimeoutPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    val TAG = TimeoutPreference::class.simpleName
    var defaultValue = 0f
    
    var savedValue: Float
        get() = getPersistedFloat(defaultValue)
        set(value) {
            persistFloat(value)
        }
    
    override fun setDefaultValue(defaultValue: Any?) {
        require(defaultValue is Float)
        this.defaultValue = defaultValue
        super.setDefaultValue(defaultValue)
    }
    
    override fun getSummary(): CharSequence {
        return String.format(super.summary.toString(), savedValue)
    }
    
    override fun onClick() {
        require(sharedPreferences != null)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.timeoutpreference_dialog, null)
        dialogView.findViewById<TextView>(android.R.id.title).text = title
        val editText = dialogView.findViewById<EditText>(R.id.editText)
        editText.setText(savedValue.toString())
        AlertDialog.Builder(context).apply {
            setView(dialogView)
        }.setPositiveButton(android.R.string.ok) { _, _ ->
            val result = editText.text.toString().toFloatOrNull() ?: 0f
            if (callChangeListener(result)) savedValue = result
            notifyChanged()
        }.setNegativeButton(android.R.string.cancel, null).show()
    }
}
