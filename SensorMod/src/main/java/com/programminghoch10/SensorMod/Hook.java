package com.programminghoch10.SensorMod;

import static com.programminghoch10.SensorMod.Common.DEFAULT_SENSOR_STATE;
import static com.programminghoch10.SensorMod.Common.getKey;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) return;
        SharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, "sensors");
        Class<SensorManager> systemSensorManager =
            (Class<SensorManager>) XposedHelpers.findClass("android.hardware.SystemSensorManager", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(
            systemSensorManager, "getFullSensorList", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.hasThrowable()) return;
                    Log.d("Logger", "afterHookedMethod: filter getFullSensorList");
                    List<Sensor> result = (List<Sensor>) param.getResult();
                    result = result.stream().filter(sensor -> sharedPreferences.getBoolean(getKey(sensor), DEFAULT_SENSOR_STATE)).toList();
                    param.setResult(result);
                }
            }
        );
        Log.d("Logger", "handleLoadPackage: Hooked SystemSensorManager");
    }
}
