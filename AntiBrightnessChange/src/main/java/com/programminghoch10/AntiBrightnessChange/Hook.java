package com.programminghoch10.AntiBrightnessChange;

import static android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;

import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("android")) {
            XposedHelpers.findAndHookMethod("com.android.server.wm.RootWindowContainer", lpparam.classLoader, "handleNotObscuredLocked",
                    "com.android.server.wm.WindowState", boolean.class, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            XposedHelpers.setFloatField(param.thisObject, "mScreenBrightnessOverride", Float.NaN);
                        }
                    });
            return;
        }
        
        XposedHelpers.findAndHookMethod(WindowManager.LayoutParams.class, "copyFrom", WindowManager.LayoutParams.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) param.args[0];
                layoutParams.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
                layoutParams.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            }
        });
    }
}
