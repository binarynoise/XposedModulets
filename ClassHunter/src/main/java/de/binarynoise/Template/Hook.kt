package de.binarynoise.ClassHunter

import android.annotation.SuppressLint
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

const val TAG = "Hook"

@SuppressLint("PrivateApi", "MissingPermission")
class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.i(TAG, "*".repeat(50))
        Log.i(TAG, "loading package: ${lpparam.packageName}")
        Log.i(TAG, "hunting for ${BuildConfig.targetClass}")
        
        val classLoaders = mutableSetOf<ClassLoader>()
        
        val packageClassLoader: ClassLoader = lpparam.classLoader
        collectParents(packageClassLoader, "packageClassLoader", classLoaders)
        
        val moduleClassLoader: ClassLoader = this::class.java.classLoader!!
        collectParents(moduleClassLoader, "moduleClassLoader", classLoaders)
        
        val systemClassLoader: ClassLoader = ClassLoader.getSystemClassLoader()
        collectParents(systemClassLoader, "systemClassLoader", classLoaders)
        
        val bootClassLoader = classLoaders.find { it.javaClass.simpleName == "BootClassLoader" }!!
        collectParents(bootClassLoader, "bootClassLoader", classLoaders)
        
        Log.i(TAG, "currently known classloaders:")
        classLoaders.forEach {
            Log.d(TAG, "${it.toObjectString()}" + " - " + it.toString())
            
            try {
                val cls = Class.forName(BuildConfig.targetClass, false, it)
                Log.w(TAG, " - found class: ${cls.name}")
                
                Log.i(TAG, " - methods:")
                cls.declaredMethods.forEach {
                    Log.d(TAG, "  - ${it.returnType.name} ${it.name}(${it.parameters.joinToString(", ") { it.name + " " + it.type.name }})")
                }
                
                Log.i(TAG, " - fields:")
                cls.declaredFields.forEach {
                    Log.d(TAG, "  - ${it.type.name} ${it.name}")
                }
            } catch (_: Throwable) {
            
            }
        }
    }
    
    private fun collectParents(classLoader: ClassLoader, name: String, classLoaders: MutableSet<ClassLoader>) {
        generateSequence(classLoader) { it.parent }.forEach {
            Log.d(TAG, name + " - ${it.toObjectString()}")
            classLoaders.add(it)
        }
    }
}

fun Any?.toObjectString(): String {
    if (this == null) return "null"
    return this::class.simpleName + "@" + Integer.toHexString(hashCode())
}
