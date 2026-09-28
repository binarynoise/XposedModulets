package com.programminghoch10.CodecMod

import java.lang.reflect.InvocationTargetException
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Hook : IXposedHookLoadPackage {
    fun getFilteredMediaCodecInfos(unfilteredMediaCodecInfos: Array<MediaCodecInfo>): Array<MediaCodecInfo> {
        val codecStore = CodecStore()
        return unfilteredMediaCodecInfos.map { mediaCodecInfo -> MediaCodecInfoWrapper(mediaCodecInfo) }
            .filter { mediaCodecInfo -> codecStore.getCodecPreference(mediaCodecInfo) }
            .map { it.originalMediaCodecInfo }
            .toTypedArray()
    }
    
    @get:Throws(InvocationTargetException::class, IllegalAccessException::class)
    @get:TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @get:SuppressLint("UseRequiresApi")
    val filteredMediaCodecInfos: Array<MediaCodecInfo>
        // helper function, only to be used on <LOLLIPOP
        get() {
            val mediaCodecs: MutableList<MediaCodecInfo> = mutableListOf()
            val codecCount = XposedBridge.invokeOriginalMethod(
                XposedHelpers.findMethodExact(
                    MediaCodecList::class.java,
                    "getCodecCount",
                    emptyArray<Any>(),
                ),
                null,
                null,
            ) as Int
            for (i in 0..<codecCount) mediaCodecs.add(
                XposedBridge.invokeOriginalMethod(
                    XposedHelpers.findMethodExact(
                        MediaCodecList::class.java,
                        "getCodecInfoAt",
                        Int::class.java,
                    ),
                    null,
                    arrayOf<Any>(i),
                ) as MediaCodecInfo
            )
            return getFilteredMediaCodecInfos(mediaCodecs.toTypedArray())
        }
    
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            XposedHelpers.findAndHookMethod(
                MediaCodecList::class.java, "getCodecInfos", object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Array<MediaCodecInfo> {
                        val mediaCodecInfos = XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args) as Array<MediaCodecInfo>
                        if (mediaCodecInfos.isEmpty()) return mediaCodecInfos
                        return getFilteredMediaCodecInfos(mediaCodecInfos)
                    }
                })
            
            // re-implementations of deprecated methods for compatibility
            XposedHelpers.findAndHookMethod(
                MediaCodecList::class.java,
                "getCodecCount",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Int {
                        return MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos.size
                    }
                },
            )
            XposedHelpers.findAndHookMethod(
                MediaCodecList::class.java,
                "getCodecInfoAt",
                Int::class.javaPrimitiveType,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): MediaCodecInfo {
                        val position = param.args[0] as Int
                        val mediaCodecInfos = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos
                        require(position in mediaCodecInfos.indices)
                        return mediaCodecInfos[position]
                    }
                },
            )
        } else {
            XposedHelpers.findAndHookMethod(
                MediaCodecList::class.java,
                "getCodecCount",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Int {
                        return filteredMediaCodecInfos.size
                    }
                },
            )
            XposedHelpers.findAndHookMethod(
                MediaCodecList::class.java,
                "getCodecInfoAt",
                Int::class.javaPrimitiveType,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): MediaCodecInfo {
                        val position = param.args[0] as Int
                        require(!(position < 0 || position >= filteredMediaCodecInfos.size))
                        return filteredMediaCodecInfos[position]
                    }
                },
            )
        }
    }
}
