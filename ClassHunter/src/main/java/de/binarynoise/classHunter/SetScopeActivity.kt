package de.binarynoise.classHunter

import kotlin.concurrent.thread
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.topjohnwu.superuser.Shell
import de.binarynoise.ClassHunter.BuildConfig
import de.binarynoise.ClassHunter.R
import de.binarynoise.logger.Logger

class SetScopeActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_scope_activity)
        
        val setScopeButton = findViewById<Button>(R.id.set_scope_button)
        val rebootButton = findViewById<Button>(R.id.reboot_button)
        val list = findViewById<LinearLayout>(R.id.log)
        
        setScopeButton.setOnClickListener {
            thread {
                try {
                    log(list, "requesting root...")
                    Shell.enableVerboseLogging = BuildConfig.DEBUG
                    Shell.getShell()
                    log(list, "got root")
                    
                    val lsposedDB = """/data/adb/lspd/config/modules_config.db"""
                    
                    log(list, Shell.cmd("/system/bin/id").exec().out.joinToString())
                    
                    val mid: Int =
                        Shell.cmd("""sqlite3 $lsposedDB "SELECT mid FROM modules WHERE module_pkg_name = '${BuildConfig.APPLICATION_ID}';"""")
                            .exec()
                            .let { result ->
                                check(result.isSuccess) { "Error:\n${result.err.joinToString("\n")}" }
                                result.out.single().toInt()
                            }
                    log(list, "my mid: $mid")
                    
                    log(list, "querying packages...")
                    val packages: MutableList<String> = Shell.cmd("pm list packages").exec().let { result ->
                        check(result.isSuccess) { "Error:\n${result.err.joinToString("\n")}" }
                        result.out.asSequence().map { it.substringAfter(":") }.toMutableList()
                    }
                    packages += "system"
                    
                    log(list, "packages: ${packages.size}")
                    
                    log(list, "clearing scope...")
                    Shell.cmd("""sqlite3 $lsposedDB "delete from scope where mid=$mid";""").exec().let { result ->
                        check(result.isSuccess) { "Error:\n${result.err.joinToString("\n")}" }
                    }
                    
                    log(list, "setting scope...")
                    Shell.cmd("""sqlite3 $lsposedDB "INSERT INTO scope (mid, app_pkg_name, user_id) VALUES ${packages.joinToString { "($mid, '$it', 0)" }};"""")
                        .exec()
                        .let { result ->
                            check(result.isSuccess) { "Error:\n${result.err.joinToString("\n")}" }
                        }
                    log(list, "done")
                } catch (t: Throwable) {
                    log(list, "Failed", t)
                }
            }
        }
        
        rebootButton.setOnClickListener {
            thread {
                try {
                    log(list, "rebooting...")
                    Shell.cmd("svc power reboot").exec()
                } catch (t: Throwable) {
                    log(list, "Failed", t)
                }
            }
        }
    }
    
    @Suppress("NOTHING_TO_INLINE")
    @SuppressLint("SetTextI18n")
    private inline fun log(list: LinearLayout, message: String, throwable: Throwable? = null) {
        if (throwable == null) Logger.log(message) else Logger.log(message, throwable)
        runOnUiThread {
            list.addView(TextView(this).apply {
                if (throwable == null) {
                    text = message
                } else {
                    text = message + "\n" + throwable.message
                }
            })
        }
    }
}
