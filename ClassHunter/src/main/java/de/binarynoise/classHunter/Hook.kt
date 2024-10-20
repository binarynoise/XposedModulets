package de.binarynoise.classHunter

import android.util.Log
import de.binarynoise.ClassHunter.BuildConfig
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

const val TAG = "Hook"

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.i(TAG, "*".repeat(50))
        Log.i(TAG, "loading package: ${lpparam.packageName}")
        Log.i(TAG, "hunting for ${BuildConfig.targetClass.joinToString()}")
        
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
            Log.v(TAG, "${it.toObjectString()} - $it")
            
            BuildConfig.targetClass.forEach { className ->
                try {
                    val cls = Class.forName(className, false, it)
                    Log.i(TAG, " - found class: ${cls.name} in package ${lpparam.packageName} by $it")
                    
                    Log.i(TAG, " - methods:")
                    cls.declaredMethods.forEach { m ->
                        Log.v(TAG, "  - ${m.returnType.name} ${m.name}(${m.parameters.joinToString(", ") { p -> p.name + " " + p.type.name }})")
                    }
                    
                    Log.i(TAG, " - fields:")
                    cls.declaredFields.forEach { f ->
                        Log.v(TAG, "  - ${f.type.name} ${f.name}")
                    }
                } catch (_: Throwable) {
                }
            }
        }
    }
    
    private fun collectParents(classLoader: ClassLoader, name: String, classLoaders: MutableSet<ClassLoader>) {
        generateSequence(classLoader) { it.parent }.forEach {
            Log.v(TAG, name + " - ${it.toObjectString()}")
            classLoaders.add(it)
        }
    }
}

fun Any?.toObjectString(): String {
    if (this == null) return "null"
    return this::class.simpleName + "@" + Integer.toHexString(hashCode())
}
