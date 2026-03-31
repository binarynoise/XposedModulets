package de.binarynoise.HideAbi

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import de.binarynoise.HideAbi.BuildConfig.SHARED_PREFERENCES_NAME

class AbisActivity : ComponentActivity(R.layout.abis_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<TextView>(R.id.supported_abis).text = Build.SUPPORTED_ABIS.joinToString("\n")
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_WORLD_READABLE)
        findViewById<TextView>(R.id.preferences).text = sharedPreferences.all.entries.joinToString("\n") { (k, v) -> "$k -> $v" }
    }
}
