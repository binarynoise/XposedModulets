package de.binarynoise.betterBluetoothDeviceSort

import android.bluetooth.BluetoothDevice
import android.os.Build
import de.binarynoise.logger.Logger.log
import de.binarynoise.reflection.cast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

class Hook : IXposedHookLoadPackage {
    
    @Suppress("RemoveExplicitTypeArguments") // Comparator.naturalOrder<String>() does need type info so the other lambda gets inferred correctly
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.bluetooth") return
        log("handleLoadPackage: betterBluetoothDeviceSort ${lpparam.packageName}")
        
        val managerClass = when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.BAKLAVA -> "com.android.bluetooth.btservice.storage.DatabaseManager"
            else -> "com.android.bluetooth.storage.BluetoothStorageManager"
        }
        
        XposedHelpers.findAndHookMethod(
            managerClass, lpparam.classLoader, "getMostRecentlyConnectedDevices",
            object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    log("called getMostRecentlyConnectedDevices")
                    with(param) {
                        if (throwable != null || result == null) return
                        @Suppress("UNCHECKED_CAST")
                        val unsorted = result as MutableList<BluetoothDevice>
                        log("${unsorted.size} devices")
                        
                        val mRemoteDevices = when {
                            Build.VERSION.SDK_INT <= Build.VERSION_CODES.BAKLAVA -> {
                                val mAdapterService = XposedHelpers.getObjectField(param.thisObject, "mAdapterService")
                                XposedHelpers.getObjectField(mAdapterService, "mRemoteDevices")
                            }
                            else -> {
                                val adapterService = XposedHelpers.getObjectField(param.thisObject, "adapterService")
                                XposedHelpers.getObjectField(adapterService, "mRemoteDevices")
                            }
                        }
                        val map = unsorted.associateWith { XposedHelpers.callMethod(mRemoteDevices, "getDeviceProperties", it) }
                        
                        val (bonded, others) = when {
                            Build.VERSION.SDK_INT <= Build.VERSION_CODES.BAKLAVA -> {
                                val mMetadataCache = XposedHelpers.getObjectField(param.thisObject, "mMetadataCache").cast<Map<String, *>>()
                                unsorted.partition { mMetadataCache.containsKey(it.address) }
                            }
                            else -> {
                                val currentStorage = XposedHelpers.callMethod(param.thisObject, "getCurrentStorage")
                                val devicesMap = XposedHelpers.callMethod(currentStorage, "getDevicesMap").cast<Map<String, *>>()
                                unsorted.partition {
                                    devicesMap.containsKey(it.address)
                                }
                            }
                        }
                        log("${bonded.size} bonded, ${others.size} others")
                        
                        val comparator = Comparator.comparing<BluetoothDevice, String>({
                            val properties = map[it] ?: run {
                                log("${it.address} not found")
                                return@comparing null
                            }
                            val mAlias = XposedHelpers.getObjectField(properties, "mAlias") as? String?
                            val mName = XposedHelpers.getObjectField(properties, "mName") as? String?
                            log("alias: $mAlias | name: $mName")
                            return@comparing mAlias ?: mName
                        }, Comparator.nullsLast(Comparator.naturalOrder<String>()))
                        
                        val res = ArrayList<BluetoothDevice>()
                        res.addAll(bonded.toSortedSet(comparator))
                        res.addAll(others)
                        
                        result = res
                    }
                }
            },
        )
        
        log("Hooked $managerClass.getMostRecentlyConnectedDevices")
    }
}
