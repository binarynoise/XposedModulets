package de.binarynoise.classHunter

import java.net.URLClassLoader
import java.security.SecureClassLoader
import android.os.Build
import android.util.Log
import dalvik.system.BaseDexClassLoader
import dalvik.system.DelegateLastClassLoader
import dalvik.system.DexClassLoader
import dalvik.system.InMemoryDexClassLoader
import dalvik.system.PathClassLoader
import de.binarynoise.ClassHunter.BuildConfig
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook as MethodHook

const val TAG = "Hook"

class Hook : IXposedHookLoadPackage, IXposedHookZygoteInit {
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
        classLoaders.forEach { classLoader ->
            Log.v(TAG, "${classLoader.toObjectString()} - $classLoader")
            
            BuildConfig.targetClass.forEach { className ->
                try {
                    val cls = Class.forName(className, false, classLoader)
                    Log.i(TAG, " - found class: ${cls.name} in package ${lpparam.packageName} by ${cls.toObjectString()}")
                    
                    Log.i(TAG, " - constructors:")
                    cls.declaredConstructors.map { c -> "${c.name}(${c.parameters.joinToString(", ") { p -> p.name + " " + p.type.name }})" }
                        .sorted()
                        .forEach { c ->
                            Log.v(TAG, "  - $c")
                        }
                    
                    Log.i(TAG, " - methods:")
                    cls.declaredMethods.map { m -> "${m.returnType.name} ${m.name}(${m.parameters.joinToString(", ") { p -> p.name + " " + p.type.name }})" }
                        .sorted()
                        .forEach { m ->
                            Log.v(TAG, "  - $m")
                        }
                    
                    Log.i(TAG, " - fields:")
                    cls.declaredFields.map { f -> "${f.type.name} ${f.name}" }.sorted().forEach { f ->
                        Log.v(TAG, "  - $f")
                    }
                } catch (_: Throwable) {
                }
            }
        }
    }
    
    private fun collectParents(classLoader: ClassLoader, name: String, classLoaders: MutableSet<ClassLoader>) {
        classLoaders.addAll(generateSequence(classLoader) { it.parent }.onEach {
            Log.v(TAG, name + " - ${it.toObjectString()}")
        })
    }
    
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        val classLoaderClasses = mutableSetOf<Class<*>>(
            ClassLoader::class.java,
            SecureClassLoader::class.java,
            URLClassLoader::class.java,
            BaseDexClassLoader::class.java,
            PathClassLoader::class.java,
            InMemoryDexClassLoader::class.java,
            DexClassLoader::class.java,
            ClassLoader.getSystemClassLoader()::class.java, // BootClassLoader
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            classLoaderClasses.add(DelegateLastClassLoader::class.java)
        }
        
        try {
            classLoaderClasses.add(Class.forName("android.app.LoadedApk\$WarningContextClassLoader", false, null))
        } catch (_: Throwable) {
        }
        
        val targetClassesShortName = BuildConfig.targetClass.map { it.substringAfterLast(".") }.toSet()
        
        classLoaderClasses.forEach {
            val loadClassHook = object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val cls = param.result as? Class<*> ?: return
                    
                    if (cls.name.substringAfterLast(".") in targetClassesShortName) {
                        Log.i(TAG, "Maybe found class: ${cls.name} by ${param.thisObject.toObjectString()}")
                    }
                }
            }
            val loadNewClassloaderHook = object : MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (param.thisObject !is PathClassLoader) {
                        Log.v(TAG, "Created a new ClassLoader: ${param.thisObject.toObjectString()} ${param.thisObject}")
                    }
                    
                    val cls: Class<*> = param.thisObject::class.java
                    if (cls !in classLoaderClasses) {
                        Log.i(TAG, "Found a new ClassLoader class: ${cls}, created by ${cls.classLoader}")
                    }
                    try {
                        // Will hang the device:
                        // XposedBridge.hookAllConstructors(it, loadNewClassloaderHook) 
                        // XposedBridge.hookAllMethods(it, "loadClass", loadClassHook)
                    } catch (_: Throwable) {
                    }
                }
            }
            
            try {
                XposedBridge.hookAllConstructors(it, loadNewClassloaderHook)
                XposedBridge.hookAllMethods(it, "loadClass", loadClassHook)
            } catch (_: Throwable) {
            }
        }
        
        Log.i(TAG, "Zygote looking for ${targetClassesShortName.joinToString()} in ${classLoaderClasses.size} ClassLoader classes")
    }
}

fun Any?.toObjectString(): String {
    if (this == null) return "null"
    return this::class.simpleName + "@" + Integer.toHexString(hashCode())
}
