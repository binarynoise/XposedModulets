package de.binarynoise.betterVerboseWiFiLogging

import kotlin.concurrent.thread
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings

class RestartSettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thread {
            Runtime.getRuntime().exec("su -c am force-stop com.android.settings").waitFor()
            runOnUiThread {
                try {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                } catch (_: ActivityNotFoundException) {
                }
                finish()
            }
        }
    }
}
