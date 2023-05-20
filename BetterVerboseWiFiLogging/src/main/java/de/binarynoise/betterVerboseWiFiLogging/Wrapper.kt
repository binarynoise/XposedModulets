@file:SuppressLint("PrivateApi")

package de.binarynoise.betterVerboseWiFiLogging

import java.lang.reflect.Method
import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.mWifiInfoField
import de.binarynoise.betterVerboseWiFiLogging.Wrapper.classLoader
import de.binarynoise.reflection.field
import de.binarynoise.reflection.method

const val wifitrackerlib = "com.android.wifitrackerlib"

object Wrapper {
    lateinit var classLoader: ClassLoader
}

object BaseWifiTracker {
    private val baseWiFiTrackerClass: Class<*> get() = classLoader.loadClass("$wifitrackerlib.BaseWifiTracker")
    
    fun isVerboseLoggingEnabled(): Boolean = baseWiFiTrackerClass.getDeclaredMethod("isVerboseLoggingEnabled").invoke(null) as Boolean
}

object Utils {
    val wifiTrackerLibUtilsClass: Class<*> get() = classLoader.loadClass("$wifitrackerlib.Utils")
}

object WifiEntry {
    val wifiEntryClass: Class<*> = classLoader.loadClass("$wifitrackerlib.WifiEntry")
    
    val getWifiInfoDescription: Method by method(wifiEntryClass)
    
    private const val CONNECTED_STATE_CONNECTED = 2
    fun newGetWifiInfoDescription(wifiEntry: Any): String {
        val mWifiInfo = mWifiInfoField[wifiEntry] as WifiInfo?
        
        if (getConnectedState(wifiEntry) != CONNECTED_STATE_CONNECTED || mWifiInfo == null) {
            return ""
        }
        with(mWifiInfo) {
            return buildList {
                add("$frequency\u202FMHz")
                add("bssid:${mWifiInfo.bssid}")
                add("standard=${mWifiInfo.wifiStandard}")
                add("rssi=$rssi\u202FdBm")
            }.joinToString(", ")
        }
    }
    
    val getNetworkCapabilityDescription by method(wifiEntryClass)
    val getNetworkSelectionDescription by method(wifiEntryClass)
    
    val getScanResultDescription by method(wifiEntryClass) // -> StandardWifiEntry
    
    val getConnectedState by method(wifiEntryClass)
    
    val mWifiInfoField by field(wifiEntryClass)
}

object StandardWifiEntry {
    private val standardWifiEntryClass: Class<*> = classLoader.loadClass("$wifitrackerlib.StandardWifiEntry")
    val getScanResultDescription by method(WifiEntry.wifiEntryClass) // -> StandardWifiEntry
    
    fun newGetScanResultDescription(wifiEntry: Any): String {
        @Suppress("UNCHECKED_CAST") val mTargetScanResults = mTargetScanResultsField[wifiEntry] as ArrayList<ScanResult>
        val mWifiInfo = mWifiInfoField[wifiEntry] as WifiInfo?
        if (mTargetScanResults.isEmpty()) return ""
        return mTargetScanResults.sortedBy { it.frequency }.joinToString(",\n ", "\n[", "]") {
            buildString {
                append("{")
                append(it.BSSID)
                if (mWifiInfo != null && mWifiInfo.bssid == it.BSSID) {
                    append("*")
                }
                append(", ")
                
                append(it.frequency)
                append("\u202FMHz")
                append(", ")
                
                append(it.level)
                append("\u202FdBm")
                
                // removed: WiFi-Standard
                // removed: WiFi 11BE info: mldMac, linkId, addLinks
                // removed: age in seconds
                
                append("}")
            }
        }
    }
    
    private val mTargetScanResultsField by field(standardWifiEntryClass)
}
