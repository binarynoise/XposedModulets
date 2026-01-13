package com.programminghoch10.GalaxyWearable;

import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableForcedUpdateHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final Class<?> UpdateManagerClass =
            XposedHelpers.findClass("com.samsung.android.app.twatchmanager.update.UpdateManager", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(
            UpdateManagerClass,
            "checkUpdatablePackages",
            Context.class,
            "com.samsung.android.app.twatchmanager.connectionmanager.define.WearableDevice",
            XC_MethodReplacement.returnConstant(false)
        );
    }
}
