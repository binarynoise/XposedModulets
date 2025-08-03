package com.programminghoch10.CodecMod;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) return;
        if (lpparam.packageName.equals("android")) {
            // system-wide hooking not implemented
            return;
        }
        XposedHelpers.findAndHookMethod(MediaCodecList.class, "getCodecInfos", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                MediaCodecInfo[] mediaCodecInfos = (MediaCodecInfo[]) XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                if (mediaCodecInfos.length == 0) return mediaCodecInfos;
                CodecStore codecStore = new CodecStore();
                MediaCodecInfo[] filteredMediaCodecInfos = Arrays.stream(mediaCodecInfos)
                        .filter(codecStore::getCodecPreference)
                        .toArray(MediaCodecInfo[]::new);
                return filteredMediaCodecInfos;
            }
        });
        
        // reimplementations of deprecated methods for compatibility
        XposedHelpers.findAndHookMethod(MediaCodecList.class, "getCodecCount", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return (new MediaCodecList(MediaCodecList.REGULAR_CODECS)).getCodecInfos().length;
            }
        });
        XposedHelpers.findAndHookMethod(MediaCodecList.class, "getCodecInfoAt", int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                final int position = (int) param.args[0];
                MediaCodecInfo[] mediaCodecInfos = (new MediaCodecList(MediaCodecList.REGULAR_CODECS)).getCodecInfos();
                if (position < 0 || position >= mediaCodecInfos.length) throw new IllegalArgumentException();
                return mediaCodecInfos[position];
            }
        });
    }
}
