package de.binarynoise.AutomaticAdvancedSettingsExpander

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class ExpandablePreferenceHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.BAKLAVA) return
        
        val ExpandablePreferenceClass = XposedHelpers.findClass("com.android.settingslib.widget.ExpandablePreference", lpparam.classLoader)
        XposedHelpers.findAndHookConstructor(
            ExpandablePreferenceClass, Context::class.java, AttributeSet::class.java, Int::class.java, Int::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    XposedHelpers.setBooleanField(param.thisObject, "isExpanded", true)
                }
            },
        )
    }
}
