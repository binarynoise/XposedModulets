package com.programminghoch10.GalaxyWearable;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DisableTelephonyPermissionHook implements IXposedHookLoadPackage {
    /*
    The app says the phone permission is used to "synchronize contacts".
    I think this is wrong, since contacts aren't requested, 
    instead I think read-only access to the phone number and the phone state are used
    to automatically enable transparency mode while the user is in a call.
    Either way, this should be an optional permission.
     */
    
    @SuppressWarnings("unchecked")
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.samsung.android.app.watchmanager")) return;
        Class<?> PermissionsUtilsClass =
            XposedHelpers.findClass("com.samsung.android.app.watchmanager.setupwizard.permission.PermissionUtils", lpparam.classLoader);
        ArrayList<String> INITIAL_PERMISSION = (ArrayList<String>) XposedHelpers.getStaticObjectField(PermissionsUtilsClass, "INITIAL_PERMISSION");
        INITIAL_PERMISSION.remove("android.permission.READ_PHONE_STATE");
        INITIAL_PERMISSION.remove("android.permission.READ_PHONE_NUMBERS");
    }
}
