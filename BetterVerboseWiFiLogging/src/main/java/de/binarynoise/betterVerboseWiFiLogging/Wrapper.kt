@file:SuppressLint("PrivateApi")

package de.binarynoise.betterVerboseWiFiLogging

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.os.Build
import de.binarynoise.betterVerboseWiFiLogging.WifiEntry.mWifiInfoField
import de.binarynoise.betterVerboseWiFiLogging.Wrapper.classLoader
import de.binarynoise.reflection.findDeclaredField
import de.binarynoise.reflection.findDeclaredMethod

const val wifitrackerlib = "com.android.wifitrackerlib"

object Wrapper {
    lateinit var classLoader: ClassLoader
}

object BaseWifiTracker {
    val baseWiFiTrackerClass: Class<*> = classLoader.loadClass("$wifitrackerlib.BaseWifiTracker")
    
    fun isVerboseLoggingEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < 34) return baseWiFiTrackerClass.getDeclaredMethod("isVerboseLoggingEnabled").invoke(null) as Boolean
        else {
            // that actually is the fix, the original code doesn't check for this either anymore
            // https://cs.android.com/android/_/android/platform/frameworks/opt/net/wifi/+/main:libs/WifiTrackerLib/src/com/android/wifitrackerlib/Utils.java;l=529;bpv=0;bpt=0
            return true
        }
    }
}

object Utils {
    val wifiTrackerLibUtilsClass: Class<*> get() = classLoader.loadClass("$wifitrackerlib.Utils")
}

val channelMap = mapOf(
    // 2.4 GHz (802.11b/g/n/ax)
    2412 to 1,
    2417 to 2,
    2422 to 3,
    2427 to 4,
    2432 to 5,
    2437 to 6,
    2442 to 7,
    2447 to 8,
    2452 to 9,
    2457 to 10,
    2462 to 11,
    2467 to 12,
    2472 to 13,
    2484 to 14,
    // 5 GHz (802.11a/h/n/ac/ax)
    5160 to 32,
    5180 to 36,
    5190 to 38,
    5200 to 40,
    5210 to 42,
    5220 to 44,
    5230 to 46,
    5240 to 48,
    5250 to 50,
    5260 to 52,
    5270 to 54,
    5280 to 56,
    5290 to 58,
    5300 to 60,
    5310 to 62,
    5320 to 64,
    5340 to 68,
    5480 to 96,
    5500 to 100,
    5510 to 102,
    5520 to 104,
    5530 to 106,
    5540 to 108,
    5550 to 110,
    5560 to 112,
    5570 to 114,
    5580 to 116,
    5590 to 118,
    5600 to 120,
    5610 to 122,
    5620 to 124,
    5630 to 126,
    5640 to 128,
    5660 to 132,
    5670 to 134,
    5680 to 136,
    5690 to 138,
    5700 to 140,
    5710 to 142,
    5720 to 144,
    5745 to 149,
    5755 to 151,
    5765 to 153,
    5775 to 155,
    5785 to 157,
    5795 to 159,
    5805 to 161,
    5815 to 163,
    5825 to 165,
    5835 to 167,
    5845 to 169,
    5855 to 171,
    5865 to 173,
    5875 to 175,
    5885 to 177,
    // 6 GHz (802.11ax and 802.11be)
    5935 to 2,
    5955 to 1,
    5975 to 5,
    5995 to 9,
    6015 to 13,
    6035 to 17,
    6055 to 21,
    6075 to 25,
    6095 to 29,
    6115 to 33,
    6135 to 37,
    6155 to 41,
    6175 to 45,
    6195 to 49,
    6215 to 53,
    6235 to 57,
    6255 to 61,
    6275 to 65,
    6295 to 69,
    6315 to 73,
    6335 to 77,
    6355 to 81,
    6375 to 85,
    6395 to 89,
    6415 to 93,
    6435 to 97,
    6455 to 101,
    6475 to 105,
    6495 to 109,
    6515 to 113,
    6535 to 117,
    6555 to 121,
    6575 to 125,
    6595 to 129,
    6615 to 133,
    6635 to 137,
    6655 to 141,
    6675 to 145,
    6695 to 149,
    6715 to 153,
    6735 to 157,
    6755 to 161,
    6775 to 165,
    6795 to 169,
    6815 to 173,
    6835 to 177,
    6855 to 181,
    6875 to 185,
    6895 to 189,
    6915 to 193,
    6935 to 197,
    6955 to 201,
    6975 to 205,
    6995 to 209,
    7015 to 213,
    7035 to 217,
    7055 to 221,
    7075 to 225,
    7095 to 229,
    7115 to 233,
    
    // 3.65 GHz (802.11y)
    3660 to 132,
    3665 to 133,
    3670 to 134,
    3680 to 136,
    3685 to 137,
    3690 to 138,
    // 4.9–5.0 GHz (802.11j)
    4920 to 184,
    4940 to 188,
    4960 to 192,
    4980 to 196,
    5040 to 8,
    5060 to 12,
    5080 to 16,
    // 5.9 GHz (802.11p)
    5860 to 172,
    5870 to 174,
    5880 to 176,
    5890 to 178,
    5900 to 180,
    5910 to 182,
    5915 to 183,
    5920 to 184,
    5935 to 187,
    5940 to 188,
    5945 to 189,
    5960 to 192,
    5980 to 196,
)

object WifiEntry {
    val wifiEntryClass: Class<*> = classLoader.loadClass("$wifitrackerlib.WifiEntry")
    
    val getWifiInfoDescription = wifiEntryClass.findDeclaredMethod("getWifiInfoDescription")
    
    private const val CONNECTED_STATE_CONNECTED = 2
    fun newGetWifiInfoDescription(wifiEntry: Any): String {
        val mWifiInfo = mWifiInfoField[wifiEntry] as WifiInfo?
        
        if (getConnectedState(wifiEntry) != CONNECTED_STATE_CONNECTED || mWifiInfo == null) {
            return ""
        }
        with(mWifiInfo) {
            return buildString {
                append("$frequency\u202FMHz")
                channelMap[frequency]?.let {
                    append("\u202F($it)")
                }
                append(", ")
                append("bssid:${mWifiInfo.bssid}")
                append(", ")
                append("standard=${mWifiInfo.wifiStandard}")
                append(", ")
                append("rssi=$rssi\u202FdBm")
            }
        }
    }
    
    val getNetworkCapabilityDescription = wifiEntryClass.findDeclaredMethod("getNetworkCapabilityDescription")
    val getNetworkSelectionDescription = wifiEntryClass.findDeclaredMethod("getNetworkSelectionDescription")
    
    val getScanResultDescription = wifiEntryClass.findDeclaredMethod("getScanResultDescription") // -> StandardWifiEntry
    
    val getConnectedState = wifiEntryClass.findDeclaredMethod("getConnectedState")
    
    val mWifiInfoField = wifiEntryClass.findDeclaredField("mWifiInfo")
}

object StandardWifiEntry {
    private val standardWifiEntryClass: Class<*> = classLoader.loadClass("$wifitrackerlib.StandardWifiEntry")
    val getScanResultDescription = WifiEntry.wifiEntryClass.findDeclaredMethod("getScanResultDescription") // -> StandardWifiEntry
    
    fun newGetScanResultDescription(wifiEntry: Any): String {
        @Suppress("UNCHECKED_CAST") val mTargetScanResults = mTargetScanResultsField[wifiEntry] as ArrayList<ScanResult>
        val mWifiInfo = mWifiInfoField[wifiEntry] as WifiInfo?
        if (mTargetScanResults.isEmpty()) return ""
        return mTargetScanResults.sortedBy { it.frequency }.joinToString(",\n ", "[", "]") {
            buildString {
                append("{")
                append(it.BSSID)
                if (mWifiInfo != null && mWifiInfo.bssid == it.BSSID) {
                    append("*")
                }
                append(", ")
                
                append(it.frequency)
                append("\u202FMHz")
                channelMap[it.frequency]?.let {
                    append("\u202F($it)")
                }
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
    
    private val mTargetScanResultsField = standardWifiEntryClass.findDeclaredField("mTargetScanResults")
}
