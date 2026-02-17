package com.programminghoch10.GalaxyWearable;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableLocationPermissionHook implements IXposedHookLoadPackage {
    @SuppressWarnings("unchecked")
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.samsung.android.app.watchmanager")) return;
        Class<?> PermissionsUtilsClass =
            XposedHelpers.findClass("com.samsung.android.app.watchmanager.setupwizard.permission.PermissionUtils", lpparam.classLoader);
        ArrayList<String> INITIAL_PERMISSION = (ArrayList<String>) XposedHelpers.getStaticObjectField(PermissionsUtilsClass, "INITIAL_PERMISSION");
        INITIAL_PERMISSION.remove("android.permission.ACCESS_FINE_LOCATION");
    }
}
