package com.programminghoch10.leeway

import android.content.Context
import de.binarynoise.logger.Logger.log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class RemovePairIpSignatureCheck : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val SignatureCheckClass = try {
            XposedHelpers.findClass("com.pairip.SignatureCheck", lpparam.classLoader)
        } catch (_: XposedHelpers.ClassNotFoundError) {
            log("PairIP SignatureCheck Class not found in ${lpparam.packageName}")
            return
        }
        XposedHelpers.findAndHookMethod(SignatureCheckClass, "verifyIntegrity", Context::class.java, XC_MethodReplacement.DO_NOTHING)
        XposedHelpers.findAndHookMethod(SignatureCheckClass, "verifySignatureMatches", String::class.java, XC_MethodReplacement.returnConstant(true))
    }
}
