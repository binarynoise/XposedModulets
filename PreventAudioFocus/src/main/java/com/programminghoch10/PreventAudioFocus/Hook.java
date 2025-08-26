package com.programminghoch10.PreventAudioFocus;

import android.media.AudioManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> clazz = AudioManager.class;
        if (lpparam.packageName.equals("android")) clazz = XposedHelpers.findClass("com.android.server.audio.MediaFocusControl", lpparam.classLoader);
        XposedBridge.hookAllMethods(clazz, "requestAudioFocus", XC_MethodReplacement.returnConstant(AudioManager.AUDIOFOCUS_REQUEST_GRANTED));
    }
}
