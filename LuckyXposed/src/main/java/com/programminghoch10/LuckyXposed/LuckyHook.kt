package com.programminghoch10.LuckyXposed

import kotlin.collections.map
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


class LuckyHook : IXposedHookLoadPackage {
    
    public val TAG = "Logger"
    val LogHook = object: XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            Log.d(TAG, "beforeHookedMethod: ${param.method.name}")
        }
        override fun afterHookedMethod(param: MethodHookParam) {
            Log.d(TAG, "afterHookedMethod: ${param.method.name}")
        }
    }
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        
        val moduleClassLoader = this::class.java.classLoader!!
        val BootClassLoaderClass = XposedHelpers.findClass("java.lang.BootClassLoader", moduleClassLoader)
        fun isBootClassLoader(classLoader: ClassLoader):Boolean {
            return BootClassLoaderClass.isAssignableFrom(classLoader::class.java)
        }
        fun getClassLoaderParentList(classLoader: ClassLoader) : List<ClassLoader> {
            return generateSequence(classLoader, {it.parent}).toList()
        }
        fun getClassLoaderParentListWithoutBootClassLoader(classLoader: ClassLoader) : List<ClassLoader> {
            return getClassLoaderParentList(classLoader).filterNot(::isBootClassLoader)
        }
        if (!cartesianProduct(getClassLoaderParentListWithoutBootClassLoader(moduleClassLoader), getClassLoaderParentListWithoutBootClassLoader(lpparam.classLoader))
            .any { (first, second) ->
                first == second
            }) {
            // determine top level classloader before BootClassLoader
            val topClassLoader = getClassLoaderParentListWithoutBootClassLoader(moduleClassLoader).last()
            XposedHelpers.setObjectField(topClassLoader, "parent", lpparam.classLoader)
        }
        
        /*XposedHelpers.findAndHookMethod(BillingClientClass, "newBuilder", 
            Context::class.java,
            object: XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): BillingClientBuilderStub {
                val context : Context = param.args[0] as Context
                Log.d(TAG, "replaceHookedMethod: replace newBuilder with stub context=${context}")
                return BillingClientStub.newBuilderStub(context)
            }
        })*/
        
        fun getFieldByType(obj: Any, clazz: Class<*>): Any? {
            Log.d(TAG, "getFieldByType: obj=${obj} clazz=${clazz}")
            Log.d(TAG, "getFieldByType: ${obj.javaClass.declaredFields.size}")
            Log.d(TAG, "getFieldByType: ${obj::class.java.declaredFields.map { it.type to it.type.isAssignableFrom(clazz)}.filter { it.second }}")
            return XposedHelpers.getObjectField(obj, obj::class.java.declaredFields.find { it.type.isAssignableFrom(clazz)}!!.name)
        }
        
        XposedHelpers.findAndHookMethod(BillingClient.Builder::class.java, "build", object: XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val purchasesUpdatedListener = getFieldByType(param.thisObject, PurchasesUpdatedListener::class.java) as PurchasesUpdatedListener?
                if (purchasesUpdatedListener == null) {
                    Log.w(TAG, "replaceHookedMethod: purchasesUpdatedListener is null, not hooking further")
                    return
                }
                val context = getFieldByType(param.thisObject, Context::class.java) as Context?
                if (context == null) {
                    Log.e(TAG, "replaceHookedMethod: context is null")
                    return
                }
                Log.d(TAG, "replaceHookedMethod: purchasesUpdatedListener=${purchasesUpdatedListener}")
                //param.result = BillingClientStub(context, purchasesUpdatedListener)
                param.result = BillingClientStub.newBuilderStub(context).setListener(purchasesUpdatedListener).build()
            }
        })
        
    }
}
