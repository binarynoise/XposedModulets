package de.binarynoise.betterBluetoothDeviceSort

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

@SuppressLint("PrivateApi", "MissingPermission")
class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(BluetoothAdapter::class.java, "getMostRecentlyConnectedDevices", object : MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                with(param) {
                    @Suppress("UNCHECKED_CAST") val unsorted = result as MutableList<BluetoothDevice>
                    unsorted.sortBy { a -> a.alias ?: a.name }
                }
            }
        })
    }
}
