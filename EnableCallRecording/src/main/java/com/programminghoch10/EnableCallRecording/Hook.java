package com.programminghoch10.EnableCallRecording;

import android.content.Context;

import java.util.Objects;

import com.programminghoch10.EnableCallRecord.BuildConfig;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (Objects.equals(lpparam.packageName, BuildConfig.APPLICATION_ID)) return;
        if (!Objects.equals(lpparam.packageName, "com.android.dialer")) return;
        
        XposedHelpers.findAndHookMethod(
            "com.android.dialer.callrecord.impl.CallRecorderService",
            lpparam.classLoader,
            "isEnabled",
            Context.class,
            XC_MethodReplacement.returnConstant(true)
        );
        XposedHelpers.findAndHookMethod(
            "com.android.incallui.call.CallRecorder",
            lpparam.classLoader,
            "isEnabled",
            XC_MethodReplacement.returnConstant(true)
        );
        XposedHelpers.findAndHookMethod(
            "com.android.incallui.call.CallRecorder",
            lpparam.classLoader,
            "canRecordInCurrentCountry",
            XC_MethodReplacement.returnConstant(true)
        );
    }
}
