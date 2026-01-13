package com.programminghoch10.GalaxyWearable;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableManufacturerCheckHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", "");
    }
}
