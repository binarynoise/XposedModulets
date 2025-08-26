package de.binarynoise.betterBluetoothDeviceSort

import android.bluetooth.BluetoothDevice
import de.binarynoise.logger.Logger.log
import de.binarynoise.reflection.cast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        
        XposedHelpers.findAndHookMethod(
            "com.android.bluetooth.btservice.storage.DatabaseManager", lpparam.classLoader, "getMostRecentlyConnectedDevices",
            object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    log("called getMostRecentlyConnectedDevices")
                    with(param) {
                        if (throwable != null || result == null) return
                        @Suppress("UNCHECKED_CAST")
                        val unsorted = result as MutableList<BluetoothDevice>
                        log("${unsorted.size} devices")
                        
                        val mAdapterService = XposedHelpers.getObjectField(param.thisObject, "mAdapterService")
                        val mRemoteDevices = XposedHelpers.getObjectField(mAdapterService, "mRemoteDevices")
                        val map = unsorted.associateWith { XposedHelpers.callMethod(mRemoteDevices, "getDeviceProperties", it) }
                        
                        val mMetadataCache = XposedHelpers.getObjectField(param.thisObject, "mMetadataCache").cast<Map<String, *>>()
                        val (bonded, others) = unsorted.partition { mMetadataCache.containsKey(it.address) }
                        log("${bonded.size} bonded, ${others.size} others")
                        
                        val comparator = Comparator.comparing<BluetoothDevice, String>({
                            val properties = map[it] ?: return@comparing null
                            (XposedHelpers.getObjectField(properties, "mAlias") ?: XposedHelpers.getObjectField(properties, "mName")).cast<String?>()
                        }, Comparator.nullsLast(Comparator.naturalOrder<String>()))
                        
                        result = bonded.toSortedSet(comparator).toMutableList() + others
                    }
                }
            },
        )
        
        log("Hooked getMostRecentlyConnectedDevices")
    }
}
