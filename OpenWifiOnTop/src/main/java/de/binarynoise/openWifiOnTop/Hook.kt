package de.binarynoise.openWifiOnTop

import de.binarynoise.reflection.cast
import de.binarynoise.reflection.findDeclaredMethod
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val WifiEntryClass = lpparam.classLoader.loadClass("com.android.wifitrackerlib.WifiEntry")
        val getSecurityTypes = WifiEntryClass.findDeclaredMethod("getSecurityTypes")
        val isSaved = WifiEntryClass.findDeclaredMethod("isSaved")
        val isPrimary = WifiEntryClass.findDeclaredMethod("isPrimaryNetwork")
        val WIFI_PICKER_COMPARATOR = XposedHelpers.getStaticObjectField(WifiEntryClass, "WIFI_PICKER_COMPARATOR").cast<Comparator<Any>>()
        
        val comparator: Comparator<Any> = Comparator { a, b ->
            val aPrimary = isPrimary.invoke(a) as Boolean
            val bPrimary = isPrimary.invoke(b) as Boolean
            
            val aSaved = isSaved.invoke(a) as Boolean
            val bSaved = isSaved.invoke(b) as Boolean
            
            val aOpen = getSecurityTypes.invoke(a)!!.cast<List<Int>>().minOrNull() == 0 // SECURITY_TYPE_OPEN
            val bOpen = getSecurityTypes.invoke(b)!!.cast<List<Int>>().minOrNull() == 0
            
            // we want
            // - primary
            // - saved && !open
            // - saved && open
            // - !saved && open
            // - !saved && !open
            
            val resultPrimary = aPrimary.compareTo(bPrimary)
            val resultSaved = aSaved.compareTo(bSaved)
            val resultOpen = aOpen.compareTo(bOpen)
            
            if (resultPrimary != 0) -resultPrimary // primary before !primary
            else if (resultSaved != 0) -resultSaved // saved before !saved
            else if (resultOpen != 0) if (aSaved) resultOpen else -resultOpen // saved != open before saved == open
            else WIFI_PICKER_COMPARATOR.compare(a, b)
        }
        
        XposedHelpers.setStaticObjectField(WifiEntryClass, "WIFI_PICKER_COMPARATOR", comparator)
    }
}
