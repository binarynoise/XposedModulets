package com.programminghoch10.RotationControl;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.view.Window;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Activity.class,
                "setRequestedOrientation", int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[0] = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
                    }
                }
        );
        XposedHelpers.findAndHookMethod("com.android.internal.policy.PhoneWindow", lpparam.classLoader,
                "generateLayout", "com.android.internal.policy.DecorView", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        Context context = ((Window) param.thisObject).getContext();
                        
                        while (context instanceof ContextWrapper) {
                            if (context instanceof Activity activity) {
                                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                                return;
                            }
                            context = ((ContextWrapper) context).getBaseContext();
                        }
                    }
                }
        );
    }
}
