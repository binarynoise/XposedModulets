package com.programminghoch10.CodecMod;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    
    MediaCodecInfo[] getFilteredMediaCodecInfos(MediaCodecInfo[] unfilteredMediaCodecInfos) {
        CodecStore codecStore = new CodecStore();
        return Arrays.stream(unfilteredMediaCodecInfos)
                .map(MediaCodecInfoWrapper::new)
                .filter(codecStore::getCodecPreference)
                .map(MediaCodecInfoWrapper::getOriginalMediaCodecInfo)
                .toArray(MediaCodecInfo[]::new);
    }
    
    // helper function, only to be used on <LOLLIPOP
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    MediaCodecInfo[] getFilteredMediaCodecInfos() throws InvocationTargetException, IllegalAccessException {
        List<MediaCodecInfo> mediaCodecs = new LinkedList<>();
        final int codecCount = (int) XposedBridge.invokeOriginalMethod(XposedHelpers.findMethodExact(MediaCodecList.class, "getCodecCount"), null, null);
        for (int i = 0; i < codecCount; i++)
            mediaCodecs.add((MediaCodecInfo) XposedBridge.invokeOriginalMethod(XposedHelpers.findMethodExact(MediaCodecList.class, "getCodecInfoAt"), null, new Object[]{i}));
        return getFilteredMediaCodecInfos(mediaCodecs.toArray(MediaCodecInfo[]::new));
    }
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) return;
        if (lpparam.packageName.equals("android")) {
            // system-wide hooking not implemented
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            XposedHelpers.findAndHookMethod(MediaCodecList.class, "getCodecInfos", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    MediaCodecInfo[] mediaCodecInfos = (MediaCodecInfo[]) XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    if (mediaCodecInfos.length == 0) return mediaCodecInfos;
                    return getFilteredMediaCodecInfos(mediaCodecInfos);
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
        } else {
            XposedHelpers.findAndHookMethod(MediaCodecList.class, "getCodecCount", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return getFilteredMediaCodecInfos().length;
                }
            });
            XposedHelpers.findAndHookMethod(MediaCodecList.class, "getCodecInfoAt", int.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    final int position = (int) param.args[0];
                    MediaCodecInfo[] mediaCodecInfos = getFilteredMediaCodecInfos();
                    if (position < 0 || position >= mediaCodecInfos.length) throw new IllegalArgumentException();
                    return mediaCodecInfos[position];
                }
            });
        }
        
    }
}
